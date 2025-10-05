package com.telemune.config;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


	@Configuration
	public class CvmDbConfig {

	    @Bean
	    @ConfigurationProperties("spring.datasource.cvm")
	    public DataSourceProperties cvmDataSourceProperties() {
	        return new DataSourceProperties();
	    }

	    @Bean
	    public DataSource cvmDataSource() {
	        return cvmDataSourceProperties().initializeDataSourceBuilder().build();
	    }

	    @Bean
	    public JdbcTemplate cvmJdbcTemplate() {
	        return new JdbcTemplate(cvmDataSource());
	    }
	}