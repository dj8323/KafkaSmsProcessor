package com.telemune.cvm.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class CvmRepository {

    private static final Logger logger = LoggerFactory.getLogger(CvmRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public CvmRepository(@Qualifier("cvmJdbcTemplate") JdbcTemplate jdbcTemplate) {
        logger.debug("CvmRepository initialized with cvmJdbcTemplate");
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
    public int updateDeliveryStatus(String refId, String status, String time) {
        logger.info("Updating delivery_status for refId={}, status={}, time={}", refId, status, time);

        String sql = "UPDATE gmat_log_info " +
                     "SET delivery_status = ?, delivery_time = ? " +
                     "WHERE JSON_UNQUOTE(JSON_EXTRACT(others, '$.ref_id')) = ?";

        int rows = jdbcTemplate.update(sql, status, time, refId);

        if (rows > 0) {
            logger.info("✅ Successfully updated {} record(s) for refId={}", rows, refId);
        } else {
            logger.warn("⚠️ No records updated for refId={}. Possible mismatch or already updated.", refId);
        }

        return rows;
    }
}
