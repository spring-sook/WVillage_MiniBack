package com.wvillage.wvillageJdbc.dao;


import com.wvillage.wvillageJdbc.vo.MemberVO;
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

    public MemberVO getUserProfile(String email) {
        String sql = "SELECT EMAIL, NICKNAME, PROFILE_IMG, SCORE, REPORT_COUNT from MEMBER WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new userInfoRowMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class userInfoRowMapper implements RowMapper<MemberVO> {
        @Override
        public MemberVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MemberVO(
                    rs.getString("EMAIL"),
                    rs.getString("NICKNAME"),
                    rs.getString("PROFILE_IMG"),
                    rs.getInt("SCORE"),
                    rs.getInt("REPORT_COUNT")
            );
        }
    }
}
