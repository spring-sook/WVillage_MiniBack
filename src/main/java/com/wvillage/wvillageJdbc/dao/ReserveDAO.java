package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.CommonVo;
import com.wvillage.wvillageJdbc.vo.PostVO;
import com.wvillage.wvillageJdbc.vo.ReserveVO;
import com.wvillage.wvillageJdbc.vo.ReviewVO;
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
                    rs.getTimestamp("RES_START").toLocalDateTime(),
                    rs.getTimestamp("RES_START").toLocalDateTime()
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
                                            POST_REGION
                                     FROM POST) P
                                    ON RS.RES_POST = P.POST_ID
                         LEFT JOIN REVIEW RV ON RV.REVIEW_RESERVE = RS.RES_ID
                """;

        try {
            List<CommonVo> lst = jdbcTemplate.query(sql, new Object[]{email}, new myReserveListMapper());

            for (CommonVo vo : lst) {
                log.warn(vo.getReview().getReviewContent()); // String, String 때문에 tags 대신 Content 사용
                ReviewVO rvo = new ReviewVO(vo.getReview().getReviewId(), tagsIntoString(vo.getReview().getReviewContent()));
                vo.setReview(rvo);
            }

            return lst;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class myReserveListMapper implements RowMapper<CommonVo> {
        @Override
        public CommonVo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommonVo(
                    new PostVO(
                            rs.getString("POST_ID"),
                            rs.getString("POST_TITLE"),
                            rs.getInt("POST_PRICE"),
                            rs.getString("POST_REGION"),
                            rs.getString("IMG_URL")
                    ),
                    new ReserveVO(
                            rs.getString("RES_ID"),
                            rs.getTimestamp("RES_START").toLocalDateTime(),
                            rs.getTimestamp("RES_END").toLocalDateTime(),
                            rs.getString("RES_STATE"),
                            rs.getString("RES_REASON")
                    ),
                    new ReviewVO(
                            rs.getString("REVIEW_ID"),
                            rs.getString("REVIEW_TAG")
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
                             POST_LOCATION
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
                """;

        try {
            List<CommonVo> lst = jdbcTemplate.query(sql, new Object[]{email}, new myReserveListManagementMapper());
            for (CommonVo vo : lst) {
                log.warn(vo.getReview().getReviewContent()); // String, String 때문에 tags 대신 Content 사용
                ReviewVO rvo = new ReviewVO(vo.getReview().getReviewId(), tagsIntoString(vo.getReview().getReviewContent()));
                vo.setReview(rvo);
            }

            return lst;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class myReserveListManagementMapper implements RowMapper<CommonVo> {
        @Override
        public CommonVo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommonVo(
                    new PostVO(
                            rs.getString("POST_ID"),
                            rs.getString("POST_TITLE"),
                            rs.getString("POST_REGION"),
                            rs.getString("POST_LOCATION"),
                            rs.getString("IMG_URL")
                    ),
                    new ReserveVO(
                            rs.getString("RES_ID"),
                            rs.getTimestamp("RES_START").toLocalDateTime(),
                            rs.getTimestamp("RES_END").toLocalDateTime(),
                            rs.getString("RES_STATE"),
                            rs.getString("RES_REASON")
                    ),
                    new ReviewVO(
                            rs.getString("REVIEW_ID"),
                            rs.getString("REVIEW_TAG")
                    )
            );
        }
    }

    public boolean insertReserve(ReserveVO vo) {
        String sql = """
                INSERT INTO RESERVE (RES_POST, RES_EMAIL ,RES_START, RES_END)
                VALUES (?,?,?,?);
                """;
        try{
            int rows = jdbcTemplate.update(sql, vo.getReservePost(), vo.getReserveEmail(), vo.getReserveStart(), vo.getReserveEnd());
            return rows > 0;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


}
