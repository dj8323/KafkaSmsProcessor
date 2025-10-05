package com.telemune;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
public class DataConsumerApplication {

    private static final Logger logger = LoggerFactory.getLogger(DataConsumerApplication.class);

    public static void main(String[] args) {
        // Load version info
    	String application="Kafka Data Consumer";
        String version = " 1.0.0.0";
        String buildDate = "13/09/2025";
        String author = "Dhananjay";

        logger.info("=====================================================");
        logger.info(" Application     : {}", application);
        logger.info(" Version : {}", version);
        logger.info(" Build Date  : {}", buildDate);
        logger.info(" Author      : {}", author);
        logger.info("=====================================================");

        SpringApplication.run(DataConsumerApplication.class, args);
    }
}
