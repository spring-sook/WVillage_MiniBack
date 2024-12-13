package com.wvillage.wvillageJdbc.dao;


import com.wvillage.wvillageJdbc.vo.MemberVO;
import com.wvillage.wvillageJdbc.vo.ReserveVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserProfileDAO extends BaseDAO {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_ADDR = "SELECT EMAIL, AREA_CODE FROM MEMBER WHERE EMAIL = ? ";

    // 유저 프로필 페이지의 정보 불러오기
    public MemberVO getUserProfile(String email) {
        String sql = """
                SELECT EMAIL, NICKNAME, PROFILE_IMG, SCORE, COUNT(*) AS REPORT_COUNT
                FROM REPORT R
                RIGHT JOIN (SELECT EMAIL, NICKNAME, PROFILE_IMG, SCORE
                            FROM MEMBER
                            WHERE EMAIL = ?) M ON R.REPORT_REPORTED = M.EMAIL
                GROUP BY EMAIL, NICKNAME, PROFILE_IMG, SCORE
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new userProfileInfoRowMapper());
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

    // 게시글 작성자의 정보 불러오기
    public MemberVO getPostedUserProfile(String postId) {
        String sql = """
                SELECT EMAIL, NICKNAME, PROFILE_IMG, AREA_CODE, SCORE
                FROM MEMBER
                WHERE EMAIL = (SELECT POST_EMAIL
                FROM POST
                WHERE POST_ID = ?)
                """;
        try {
            MemberVO vo = jdbcTemplate.queryForObject(sql, new Object[]{postId}, new userPostInfoRowMapper());
            vo.setAreaCode(getRegionName(vo.getAreaCode()));
            return vo;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class userPostInfoRowMapper implements RowMapper<MemberVO> {
        @Override
        public MemberVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MemberVO(
                    rs.getString("EMAIL"),
                    rs.getString("NICKNAME"),
                    rs.getString("PROFILE_IMG"),
                    rs.getString("AREA_CODE"),
                    rs.getInt("SCORE")
            );
        }
    }


    private static class userProfileInfoRowMapper implements RowMapper<MemberVO> {
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

    // 읽지 않은 알림
    public ReserveVO getNewMsg(String email) {
        String sql = """
                SELET
                    SUM(CASE WHEN R.RES_MSG_LENT = 1 AND P.POST_EMAIL = ? THEN 1 ELSE 0 END) AS LENT_MSG,
                    SUM(CASE WHEN R.RES_MSG_LENTED = 1 AND R.RES_EMAIL = ? THEN 1 ELSE 0 END) AS LENTED_MSG
                FROM RESERVE R
                LEFT JOIN POST P ON R.RES_POST = P.POST_ID;
                """;

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new newMsgRowMapper(email));
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class newMsgRowMapper implements RowMapper<ReserveVO> {
        private final String email;

        public newMsgRowMapper(String email) {
            this.email = email;
        }

        @Override
        public ReserveVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ReserveVO(
                    rs.getString(email),
                    rs.getInt("LENT_MSG"),
                    rs.getInt("LENTED_MSG")
            );
        }
    }
}
