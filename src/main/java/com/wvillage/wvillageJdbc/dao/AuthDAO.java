package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Repository
@Slf4j
public class AuthDAO {

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, String> resetTokenStore = new HashMap<>();

    public AuthDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private static final String LOGIN = "SELECT EMAIL, NAME, NICKNAME, SCORE, PROFILE_IMG, AREA_CODE, GRADE, POINT " +
            "FROM MEMBER WHERE EMAIL = ? AND PASSWORD = ?";


    private static final String SIGNUP = "INSERT INTO MEMBER (EMAIL, PASSWORD, NAME, NICKNAME, PHONE, AREA_CODE, GRADE) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";


    public MemberVO login(String email, String password) {
        try {
            return jdbcTemplate.queryForObject(LOGIN, new Object[]{email, password}, new LoginInfoRowMapper());
        } catch (DataAccessException e) {
            log.error("로그인 실패 중 오류 발생", e);
            return null;
        }
    }


    public boolean signup(MemberVO member) {
        try {
            jdbcTemplate.update(SIGNUP,
                    member.getEmail(),
                    member.getPassword(),
                    member.getName(),
                    member.getNickname(),
                    member.getPhone(),
                    member.getAreaCode(),
                    member.getGrade()
            );
            return true;
        } catch (DataAccessException e) {
            log.error("회원가입 중 오류 발생", e);
            return false;
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

    public String findEmailByNameAndPhone(String name, String phone) {
        String query = "SELECT EMAIL FROM MEMBER WHERE NAME = ? AND PHONE = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{name, phone}, String.class);
        } catch (DataAccessException e) {
            log.error("이메일 찾기 실패: 이름={}, 전화번호={}", name, phone, e);
            return null;
        }
    }

    public boolean verifyUser(String email, String phone) {
        String query = "SELECT COUNT(*) FROM MEMBER WHERE EMAIL = ? AND PHONE = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(query, new Object[]{email, phone}, Integer.class);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            log.error("사용자 인증 실패: email={}, phone={}", email, phone, e);
            return false;
        }
    }

    public boolean updatePasswordWithoutEncryption(String email, String newPassword) {
        String query = "UPDATE MEMBER SET PASSWORD = ? WHERE EMAIL = ?";
        try {
            log.info("비밀번호 업데이트 실행: email={}, newPassword={}", email, newPassword);
            int rowsAffected = jdbcTemplate.update(query, newPassword, email);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            log.error("비밀번호 업데이트 실패: email={}, error={}", email, e.getMessage());
            return false;
        }
    }
};


