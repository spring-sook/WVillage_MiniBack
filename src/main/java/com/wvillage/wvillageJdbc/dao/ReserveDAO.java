package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.ReserveVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReserveDAO {

    private final JdbcTemplate jdbcTemplate;

    public List<ReserveVO> getPostReserveList(String postId) {
        String sql = """
                SELECT RES_START, RES_END
                FROM RESERVE
                WHERE RES_POST = ? AND RES_STATE NOT IN ('deny', 'cancel')""";

        try {
            return jdbcTemplate.query(sql, new Object[]{postId}, new PostReserveMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class PostReserveMapper implements RowMapper<ReserveVO> {
        @Override
        public ReserveVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ReserveVO(
                    rs.getDate("RES_START"),
                    rs.getDate("RES_START")
            );
        }
    }
}
