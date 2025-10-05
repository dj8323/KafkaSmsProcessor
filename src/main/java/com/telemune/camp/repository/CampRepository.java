package com.telemune.camp.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class CampRepository {

    private static final Logger logger = LoggerFactory.getLogger(CampRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public CampRepository(@Qualifier("campJdbcTemplate") JdbcTemplate jdbcTemplate) {
        logger.debug("CampRepository initialized with campJdbcTemplate");
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Updates delivery status and time in gmat_report_info based on JSON ref_id inside 'others' column.
     *
     * @param refId   Reference ID to match inside the JSON field
     * @param status  Delivery status to set
     * @param time    Delivery time to set (can be null)
     * @return number of rows updated
     */
    public int updateDeliveryStatus(String refId,String msisdn, String status, String time) {
    	logger.debug("Updating delivery_status for refId={}, msisdn={}, status={}, time={}", refId, msisdn, status, time);

    		String sql = "UPDATE gmat_log_info " +
             "SET delivery_status = ?, delivery_time = ? " +
             "WHERE destination_number = ? AND ref_id = ?";

    		// Execute update
    		int rows = jdbcTemplate.update(sql, status, time, msisdn, refId);

    		if (rows > 0) {
    			logger.info("✅ Successfully updated {} record(s) for refId={} and msisdn={}", rows, refId, msisdn);
    		} else {
    			logger.warn("⚠️ No records updated for refId={} and msisdn={}. Possible mismatch or already updated.", refId, msisdn);
    		}	

    		return rows;
    }
}
