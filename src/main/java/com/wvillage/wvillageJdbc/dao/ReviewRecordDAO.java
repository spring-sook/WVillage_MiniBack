package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReviewRecordDAO {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    private final String LOAD_REVIEW_RECORD_SQL =
            "SELECT RR.REC_EMAIL, RT.TAG_CONTENT, RR.REC_COUNT " +
                    "FROM (SELECT * FROM REVIEW_RECORD WHERE REC_EMAIL = ?) RR " +
                    "JOIN REVIEW_TAG RT ON RR.REC_REVIEW = RT.TAG_ID";



    // 해당 사용자의 이메일을 인자로 받아 VO의 리스트를 반환
    public List<ReviewVO> getReviewRecord(String email) {
        try {
            return jdbcTemplate.query(LOAD_REVIEW_RECORD_SQL, new Object[]{email}, new ReviewRecordMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class ReviewRecordMapper implements RowMapper<ReviewVO> {
        @Override
        public ReviewVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ReviewVO(
                    rs.getString("REC_EMAIL"),
                    rs.getString("TAG_CONTENT"),
                    rs.getInt("REC_COUNT")
            );
        }
    }
}
