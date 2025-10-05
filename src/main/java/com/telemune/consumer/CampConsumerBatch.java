package com.telemune.consumer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemune.camp.repository.CampRepository;
import com.telemune.model.MessageData;

@Service
public class CampConsumerBatch {

    private static final Logger logger = LoggerFactory.getLogger(CampConsumerBatch.class);

    private final CampRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BlockingQueue<MessageData> queue = new LinkedBlockingQueue<>();

    @Value("${batch.size:100}")
    private int batchSize;

    @Value("${batch.initial-delay:2}")
    private int batchInitialDelay;

    @Value("${batch.interval:2}")
    private int batchInterval;

    private ScheduledExecutorService scheduler;

    public CampConsumerBatch(CampRepository repository) {
        this.repository = repository;
    }

    // Initialize scheduler after properties are injected
    @PostConstruct
    private void init() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::processBatch, batchInitialDelay, batchInterval, TimeUnit.SECONDS);
    }
    /*
    {
      "others": {
        "ref_id": "12345",
        "source": "camp"
      },
      "delivery_status": "Delivered",
      "delivery_time": "2025-09-13T12:34:56",
      "msisdn": "919876543210"
    }
     */
    @KafkaListener(topics = "camp_sms_delivery", groupId = "sms_group")
    public void consume(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            JsonNode othersNode = root.path("others");
            String refId = othersNode.path("ref_id").asText(null);
            if (refId == null) {
                logger.warn("No ref_id in message: {}", message);
                return;
            }

            String status = root.path("delivery_status").asText("Missing status");
            String deliveryTime = root.path("delivery_time").asText(null);
            if (deliveryTime == null) {
                deliveryTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }
            String msisdn = root.path("msisdn").asText("-1");

            // Add to queue for batch processing
            queue.offer(new MessageData(refId, msisdn, status, deliveryTime));
        } catch (Exception e) {
            logger.error("Failed to parse message: {}", message, e);
        }
    }

    private void processBatch() {
        List<MessageData> batch = new ArrayList<>();
        queue.drainTo(batch, batchSize);

        if (batch.isEmpty()) {
            return;
        }

        logger.info("Processing batch of {} messages", batch.size());

        for (MessageData data : batch) {
            try {
                int rows = repository.updateDeliveryStatus(data.getRefId(), data.getMsisdn(), data.getStatus(), data.getDeliveryTime());
                if (rows > 0) {
                    logger.info("✅ Updated refId={} successfully", data.getRefId());
                } else {
                    logger.warn("⚠️ No record updated for refId={}", data.getRefId());
                }
            } catch (Exception e) {
                logger.error("Failed to update refId={}", data.getRefId(), e);
            }
        }
    }
}
