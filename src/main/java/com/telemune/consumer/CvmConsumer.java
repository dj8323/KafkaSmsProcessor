package com.telemune.consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemune.cvm.repository.CvmRepository;

@Service
public class CvmConsumer {
    private static final Logger logger = LoggerFactory.getLogger(CvmConsumer.class);
    private final CvmRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CvmConsumer(CvmRepository repository) {
        logger.debug("CvmConsumer constructor called");
        this.repository = repository;
    }
/*
{
  "others": {
    "ref_id": "12345",
    "source": "camp"
  },
  "delivery_status": "Delivered",
  "delivery_time": "2025-09-13T12:34:56"
}



 */
    @KafkaListener(topics = "cvm_sms_delivery", groupId = "sms_group")
    public void consume(String message) {
        logger.info("consume called with message: {}", message);
        try {
            JsonNode root = objectMapper.readTree(message);
            logger.debug("Parsed message to JsonNode: {}", root);

            // 1. Extract 'others' JSON
            JsonNode othersNode = root.has("others") ? root.get("others") : null;
            if (othersNode == null) {
                logger.warn("No 'others' field in message: {}", message);
                return;
            }

            String refId = othersNode.has("ref_id") ? othersNode.get("ref_id").asText() : null;
            if (refId == null) {
                logger.warn("No 'ref_id' in others: {}", message);
                return;
            }

            // 2. Extract delivery_status and delivery_time (from root if present, else defaults)
            String status = root.has("delivery_status") ? root.get("delivery_status").asText() : "Delivered";
            String deliveryTime = root.has("delivery_time") ? root.get("delivery_time").asText() : null;

            logger.info("Updating delivery status for refId={}, status={}, deliveryTime={}", refId, status, deliveryTime);
            // 3. Update DB
           int rowUpdated= repository.updateDeliveryStatus(refId, status, deliveryTime);
           if (rowUpdated > 0) {
        	    logger.info("✅ Successfully updated delivery status for refId={}", refId);
        	} else {
        	    logger.warn("⚠️ No record found to update for refId={}", refId);
        	}

        } catch (Exception e) {
            logger.error("Exception in consume: ", e);
        }
    }
}