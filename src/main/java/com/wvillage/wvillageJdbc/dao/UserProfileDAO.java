package com.wvillage.wvillageJdbc.dao;


import com.wvillage.wvillageJdbc.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    private static final String GET_ADDR = "SELECT EMAIL, AREA_CODE FROM MEMBER WHERE EMAIL = ? ";

    public MemberVO getUserProfile(String email) {
        String sql = "SELECT EMAIL, NICKNAME, PROFILE_IMG, SCORE, REPORT_COUNT from MEMBER WHERE EMAIL = ? ";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new userInfoRowMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public MemberVO getAddr(String email) {
        try {
            return jdbcTemplate.queryForObject(GET_ADDR, new Object[]{email}, new addrRowMapper());
        } catch (DataAccessException e) {
            log.error("회원 이메일, 주소 조회 중 에러 발생", e);
            throw e;
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

    private static class addrRowMapper implements RowMapper<MemberVO> {
        @Override
        public MemberVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            MemberVO member = new MemberVO();
            member.setEmail(rs.getString("EMAIL"));
            member.setAreaCode(rs.getString("AREA_CODE"));
            return member;
        }
    }
}
