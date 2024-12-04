package com.wvillage.wvillageJdbc.dao;


import com.wvillage.wvillageJdbc.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserProfileDAO {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public UserProfileVO getUserProfile(String email) {
        String sql = "SELECT EMAIL, NICKNAME, PROFILE_IMG, SCORE, REPORT_COUNT from MEMBER WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new userInfoRowMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class userInfoRowMapper implements RowMapper<UserProfileVO> {
        @Override
        public UserProfileVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserProfileVO(
                    rs.getString("EMAIL"),
                    rs.getString("NICKNAME"),
                    rs.getString("PROFILE_IMG"),
                    rs.getInt("SCORE"),
                    rs.getInt("REPORT_COUNT")
            );
        }
    }
}
