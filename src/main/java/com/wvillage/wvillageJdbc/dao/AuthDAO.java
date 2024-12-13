package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
@Slf4j
public class AuthDAO {

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, String> resetTokenStore = new HashMap<>();

    public AuthDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private static final String LOGIN = "SELECT EMAIL, NAME, NICKNAME, PHONE, SCORE, PROFILE_IMG, AREA_CODE, GRADE, POINT " +
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
            String sql = "INSERT INTO member (email, password, name, nickname, phone, area_code, grade) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    member.getEmail(),
                    member.getPassword(),
                    member.getName(),
                    member.getNickname(),
                    member.getPhone(),
                    member.getAreaCode(),
                    member.getGrade());
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
                    rs.getString("PHONE"),
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
        String sql = "SELECT COUNT(*) FROM member WHERE email = ? AND phone = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email, phone);
        return count != null && count > 0;
    }

    public boolean updatePasswordWithoutEncryption(String email, String newPassword) {
        String sql = "UPDATE member SET password = ? WHERE email = ?";
        try {
            int rowsUpdated = jdbcTemplate.update(sql, newPassword, email);
            log.info("비밀번호 업데이트: rowsUpdated={}", rowsUpdated);
            return rowsUpdated > 0;
        } catch (DataAccessException e) {
            log.error("비밀번호 업데이트 실패: email={}", email, e);
            return false;
        }
    }


    public boolean updateMemberInfo(MemberVO memberVo) {
        String sql = "UPDATE member SET " +
                "name = ?, " +
                "nickname = ?, " +
                "password = ?, " +
                "phone = ?, " +
                "area_code = ? " +
                "WHERE email = ?";

        try {
            jdbcTemplate.update(sql,
                    memberVo.getName(),
                    memberVo.getNickname(),
                    memberVo.getPassword(),
                    memberVo.getPhone(),
                    memberVo.getAreaCode(),
                    memberVo.getEmail());
            return true;
        } catch (DataAccessException e) {
            log.error("회원정보 수정 중 오류 발생", e);
            return false;
        }
    }
};


