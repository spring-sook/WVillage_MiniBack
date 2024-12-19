package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.CommonVo;
import com.wvillage.wvillageJdbc.vo.PostVO;
import com.wvillage.wvillageJdbc.vo.ReserveVO;
import com.wvillage.wvillageJdbc.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReserveDAO extends BaseDAO {

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
                    getOffsetDateTime(rs.getTimestamp("RES_START")),
                    getOffsetDateTime(rs.getTimestamp("RES_END"))
            );
        }
    }

    // 내가 한 예약 보기
    public List<CommonVo> getMyReserveList(String email) {
        String sql = """
                SELECT I.IMG_URL,
                       P.POST_ID,
                       P.POST_TITLE,
                       P.POST_PRICE,
                       P.POST_REGION,
                       P.POST_LOCATION,
                       RS.RES_ID,
                       RS.RES_START,
                       RS.RES_END,
                       RS.RES_STATE,
                       RS.RES_REASON,
                       RV.REVIEW_ID,
                       RV.REVIEW_TAG
                FROM (SELECT *
                      FROM RESERVE
                      WHERE RES_EMAIL = ?) RS
                         LEFT JOIN (SELECT IMG_POST, IMG_URL
                                    FROM POST_IMG
                                    WHERE IMG_ID IN (SELECT IMG_ID
                                                     FROM (SELECT IMG_ID,
                                                                  ROW_NUMBER() OVER
                                                                      (PARTITION BY IMG_POST
                                                                      ORDER BY TO_NUMBER(SUBSTR(IMG_ID, 5))) AS rn
                                                           FROM POST_IMG)
                                                     WHERE rn = 1)) I
                                   ON I.IMG_POST = RS.RES_POST
                         LEFT JOIN (SELECT POST_ID,
                                            POST_TITLE,
                                            POST_PRICE,
                                            POST_REGION,
                                            POST_LOCATION
                                     FROM POST) P
                                    ON RS.RES_POST = P.POST_ID
                         LEFT JOIN REVIEW RV ON RV.REVIEW_RESERVE = RS.RES_ID
                ORDER BY RES_START DESC
                """;

        String msgRead = """
                UPDATE RESERVE SET
                RES_MSG_LENTED = 0
                WHERE RES_EMAIL = ?
                AND RES_MSG_LENTED = 1
                """;

        try {
            int row = jdbcTemplate.update(msgRead, email);
            log.error("{}", row);
            return jdbcTemplate.query(sql, new Object[]{email}, new myReserveListMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private class myReserveListMapper implements RowMapper<CommonVo> {
        @Override
        public CommonVo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommonVo(
                    new PostVO(
                            rs.getString("POST_ID"),
                            rs.getString("POST_TITLE"),
                            rs.getInt("POST_PRICE"),
                            getRegionName(rs.getString("POST_REGION")),
                            rs.getString("POST_LOCATION"),
                            rs.getString("IMG_URL")
                    ),
                    new ReserveVO(
                            rs.getString("RES_ID"),
                            getOffsetDateTime(rs.getTimestamp("RES_START")),
                            getOffsetDateTime(rs.getTimestamp("RES_END")),
                            rs.getString("RES_STATE"),
                            rs.getString("RES_REASON")
                    ),
                    new ReviewVO(
                            rs.getString("REVIEW_ID"),
                            tagsIntoString(rs.getString("REVIEW_TAG"))
                    )
            );
        }
    }

    // 내 게시물의 예약 관리
    public List<CommonVo> getReserveListManagement(String email) {
        String sql = """
                SELECT POST_ID,
                       POST_TITLE,
                       POST_REGION,
                       POST_LOCATION,
                       POST_PRICE,
                       IMG_URL,
                       RES_ID,
                       RES_STATE,
                       RES_START,
                       RES_END,
                       RES_REASON,
                       REVIEW_ID,
                       REVIEW_TAG
                FROM (SELECT POST_ID,
                             POST_TITLE,
                             POST_REGION,
                             POST_LOCATION,
                             POST_PRICE
                      FROM POST
                      WHERE POST_ID IN (SELECT POST_ID
                                        FROM POST
                                        WHERE POST_EMAIL = ?)) P
                        LEFT JOIN (SELECT IMG_POST, IMG_URL
                                   FROM POST_IMG
                                   WHERE IMG_ID IN (SELECT IMG_ID
                                                    FROM (SELECT IMG_ID,
                                                                 ROW_NUMBER() OVER
                                                                     (PARTITION BY IMG_POST
                                                                     ORDER BY TO_NUMBER(SUBSTR(IMG_ID, 5))) AS rn
                                                          FROM POST_IMG)
                                                    WHERE rn = 1)) I
                                   ON POST_ID = IMG_POST
                         JOIN RESERVE RS ON POST_ID = RES_POST
                         LEFT JOIN REVIEW RV ON RES_ID = REVIEW_RESERVE
                ORDER BY RES_START DESC
                """;

        String msgRead = """
                UPDATE RESERVE SET
                RES_MSG_LENT = 0
                WHERE RES_POST IN (SELECT POST_ID
                FROM POST
                WHERE POST_EMAIL = ?)
                AND RES_MSG_LENT = 1
                """;

        try {
            int row = jdbcTemplate.update(msgRead, email);
            log.error("{}", row);
            return jdbcTemplate.query(sql, new Object[]{email}, new myReserveListManagementMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private class myReserveListManagementMapper implements RowMapper<CommonVo> {
        @Override
        public CommonVo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommonVo(
                    new PostVO(
                            rs.getString("POST_ID"),
                            rs.getString("POST_TITLE"),
                            getRegionName(rs.getString("POST_REGION")),
                            rs.getString("POST_LOCATION"),
                            rs.getString("IMG_URL"),
                            rs.getInt("POST_PRICE")
                    ),
                    new ReserveVO(
                            rs.getString("RES_ID"),
                            getOffsetDateTime(rs.getTimestamp("RES_START")),
                            getOffsetDateTime(rs.getTimestamp("RES_END")),
                            rs.getString("RES_STATE"),
                            rs.getString("RES_REASON")
                    ),
                    new ReviewVO(
                            tagsIntoVo(rs.getString("REVIEW_TAG")),
                            rs.getString("REVIEW_ID")
                    )
            );
        }
    }

    // 새 예약 등록
    public boolean insertReserve(ReserveVO vo) {
        String sql = """
                INSERT INTO RESERVE (RES_POST, RES_EMAIL, RES_START, RES_END, RES_STATE)
                VALUES (?, ?, ?, ?, 'wait')
                """;
        try {
            // ZonedDateTime 또는 Instant를 사용하는 경우
            // 특정 시점의 시간을 UTC(Coordinated Universal Time)로 나타내는 객체
            Instant startInstant = vo.getReserveStart().toInstant(); // ZonedDateTime인 경우
            Instant endInstant = vo.getReserveEnd().toInstant(); // ZonedDateTime인 경우

            Timestamp startTimestamp = Timestamp.from(startInstant);
            Timestamp endTimestamp = Timestamp.from(endInstant);


            int rows = jdbcTemplate.update(sql, vo.getReservePost(), vo.getReserveEmail(), startTimestamp, endTimestamp);
            return rows > 0;
        } catch (DateTimeParseException e) {
            log.error("날짜 형식이 잘못되었습니다: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("예약 등록 실패: {}", e.getMessage());
            return false;
        }
    }

    // 예약 승인
    @Transactional
    public boolean reserveAccept(ReserveVO vo) {
        return updateReserve(vo, true);
    }

    // 예약 취소
    @Transactional
    public boolean reserveCancel(ReserveVO vo) {
        return updateReserve(vo, false);
    }


    private boolean updateReserve(ReserveVO vo, boolean isApprove) {
        String AcceptReserve = """
                UPDATE RESERVE
                SET RES_STATE = ?, RES_MSG_LENTED = 1
                WHERE RES_ID = ?
                """;

        String cancelReserve = """
                UPDATE RESERVE
                SET RES_STATE = ?, RES_MSG_LENT = 1, RES_REASON = ?
                WHERE RES_ID = ?
                """;

        String updateOwner = """
                UPDATE MEMBER
                SET POINT = POINT + ?
                WHERE EMAIL = (SELECT EMAIL
                FROM POST
                WHERE POST_ID = ?)
                """;

        String updateCustomer = """
                UPDATE MEMBER
                SET POINT = POINT - ?
                WHERE EMAIL = ?
                """;

        int totalPrice = vo.getReserveTotalPrice();
        log.info("{}", totalPrice);

        try {
            int rows;
            if (!isApprove) {
                rows = jdbcTemplate.update(cancelReserve, vo.getReserveState(), vo.getReserveReason(), vo.getReserveId());
                rows += jdbcTemplate.update(updateOwner, totalPrice, vo.getReservePost());
                rows += jdbcTemplate.update(updateCustomer, totalPrice, vo.getReserveEmail());
            } else {
                log.error(vo.toString());
                rows = jdbcTemplate.update(AcceptReserve, vo.getReserveState(), vo.getReserveId());
                rows += jdbcTemplate.update(updateOwner, totalPrice, vo.getReservePost());
                rows += jdbcTemplate.update(updateCustomer, totalPrice, vo.getReserveEmail());
            }

            return rows > 0;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    // 예약 거절, 완료
    public boolean reserveUpdate(ReserveVO vo) {
        String complete = "UPDATE RESERVE SET RES_STATE = ?, RES_MSG_LENT = ?, RES_MSG_LENTED = ? WHERE RES_ID = ?";
        String deny = "UPDATE RESERVE SET RES_STATE = ?, RES_MSG_LENTED = ?, RES_REASON = ? WHERE RES_ID = ?";


        try {
            int rows;
            if (vo.getReserveState().equals("deny")) {
                rows = jdbcTemplate.update(deny, vo.getReserveState(), 1, vo.getReserveReason(), vo.getReserveId());
            } else {
                rows = jdbcTemplate.update(complete, vo.getReserveState(), 1, 1, vo.getReserveId());
            }

            return rows > 0;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // 포인트 잔액 확인
    public int remainPoints(String email) {
        String sql = "SELECT POINT FROM MEMBER WHERE EMAIL= ? ";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        } catch (DataAccessException e) {
            log.error("포인트 잔액 확인 중 에러 : {}", e);
            throw e;
        }
    }

}
