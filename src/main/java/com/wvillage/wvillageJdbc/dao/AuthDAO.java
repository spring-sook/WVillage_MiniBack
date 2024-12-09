package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.MemberVO;
import lombok.Data;
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
public class AuthDAO {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private static final String LOGIN = "SELECT EMAIL, NAME, NICKNAME, SCORE, PROFILE_IMG, AREA_CODE, GRADE, POINT " +
                                        "FROM MEMBER WHERE EMAIL = ? AND PASSWORD = ? ";

    public MemberVO login(String email, String password) {
        try {
            return jdbcTemplate.queryForObject(LOGIN, new Object[]{email, password}, new LoginInfoRowMapper());
        } catch (DataAccessException e) {
            log.error("로그인 실패 중 오류 발생", e);
            return null;
        }
    }

    private static class LoginInfoRowMapper implements RowMapper<MemberVO> {
        @Override
        public MemberVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MemberVO(
                    rs.getString("EMAIL"),
                    rs.getString("NAME"),
                    rs.getString("NICKNAME"),
                    rs.getInt("SCORE"),
                    rs.getString("PROFILE_IMG"),
                    rs.getString("AREA_CODE"),
                    rs.getString("GRADE"),
                    rs.getInt("POINT")
            );
        }
    }
}
