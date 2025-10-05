package com.telemune.config;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


	@Configuration
	public class CampDbConfig {
	    private static final Logger logger = LoggerFactory.getLogger(CampDbConfig.class);

	    @Bean
	    @Primary
	    @ConfigurationProperties("spring.datasource.camp")
	    public DataSourceProperties campDataSourceProperties() {
	        logger.debug("Creating campDataSourceProperties bean");
	        return new DataSourceProperties();
	    }

	    @Bean
	    public DataSource campDataSource() {
	        logger.debug("Creating campDataSource bean");
	        return campDataSourceProperties().initializeDataSourceBuilder().build();
	    }

	    @Bean
	    public JdbcTemplate campJdbcTemplate() {
	        logger.debug("Creating campJdbcTemplate bean");
	        return new JdbcTemplate(campDataSource());
	    }
	}