package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.PriorityOrdered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostListDAO extends BaseDAO {
    @Autowired
    private final JdbcTemplate jdbcTemplate;


    // 특정 유저가 게시한 게시글 목록 불러오기
    public List<PostVO> getUserProfilePostList(String email) {
        String sql = """
                SELECT P.POST_ID,
                       P.POST_TITLE,
                       P.POST_PRICE,
                       P.POST_REGION,
                       I.IMG_URL,
                       P.POST_DISABLED
                FROM (SELECT IMG_POST, IMG_URL
                      FROM POST_IMG
                      WHERE IMG_ID IN (SELECT IMG_ID
                                       FROM (SELECT IMG_ID,
                                                    ROW_NUMBER() OVER
                                                        (PARTITION BY IMG_POST
                                                        ORDER BY TO_NUMBER(SUBSTR(IMG_ID, 5))) AS rn
                                             FROM POST_IMG)
                                       WHERE rn = 1)) I
                          RIGHT JOIN (SELECT POST_ID,
                                      POST_TITLE,
                                      POST_PRICE,
                                      POST_REGION,
                                      POST_DISABLED
                               FROM POST
                               WHERE POST_EMAIL = ?) P
                              ON P.POST_ID = I.IMG_POST""";
        try {
            return jdbcTemplate.query(sql, new Object[]{email}, new UserPostlistRowMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class UserPostlistRowMapper implements RowMapper<PostVO> {
        @Override
        public PostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostVO(
                    rs.getString("POST_ID"),
                    rs.getString("POST_TITLE"),
                    rs.getInt("POST_PRICE"),
                    rs.getString("POST_REGION"),
                    rs.getString("IMG_URL"),
                    rs.getBoolean("POST_DISABLED")
            );
        }
    }

    // 지역+카테고리 기준 목록 불러오기
    public List<PostVO> getCommonCategoryPostList(String region, String category) {
        String sql = """
                SELECT P.POST_ID,
                       P.POST_TITLE,
                       P.POST_PRICE,
                       P.POST_REGION,
                       I.IMG_URL,
                       P.POST_VIEW,
                       P.POST_DATE
                FROM (SELECT IMG_POST, IMG_URL
                      FROM POST_IMG
                      WHERE IMG_ID IN (SELECT IMG_ID
                                       FROM (SELECT IMG_ID,
                                                    ROW_NUMBER() OVER
                                                        (PARTITION BY IMG_POST
                                                        ORDER BY TO_NUMBER(SUBSTR(IMG_ID, 5))) AS rn
                                             FROM POST_IMG)
                                       WHERE rn = 1)) I
                         RIGHT JOIN (SELECT POST_ID,
                                      POST_TITLE,
                                      POST_PRICE,
                                      POST_REGION,
                                      POST_VIEW,
                                      POST_DATE
                               FROM POST
                               WHERE POST_REGION = ? AND POST_CATEGORY = ? AND POST_DISABLED = 0) P
                              ON P.POST_ID = I.IMG_POST""";
        try {
            return jdbcTemplate.query(sql, new Object[]{region, category}, new CommonRowMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

    // 지역 기준 카테고리 없이 목록 불러오기
    public List<PostVO> getCommonAllPostList(String region) {
        String sql = """
                SELECT P.POST_ID,
                       P.POST_TITLE,
                       P.POST_PRICE,
                       P.POST_REGION,
                       I.IMG_URL,
                       P.POST_VIEW,
                       P.POST_DATE
                FROM (SELECT IMG_POST, IMG_URL
                       FROM POST_IMG
                       WHERE IMG_ID IN (SELECT IMG_ID
                                        FROM (SELECT IMG_ID,
                                                     ROW_NUMBER() OVER
                                                         (PARTITION BY IMG_POST
                                                         ORDER BY TO_NUMBER(SUBSTR(IMG_ID, 5))) AS rn
                                              FROM POST_IMG)
                                        WHERE rn = 1)) I
                         RIGHT JOIN (SELECT POST_ID,
                                      POST_TITLE,
                                      POST_PRICE,
                                      POST_REGION,
                                      POST_VIEW,
                                      POST_DATE
                               FROM POST
                               WHERE POST_REGION = ? AND POST_DISABLED = 0) P
                              ON P.POST_ID = I.IMG_POST""";
        try {
            return jdbcTemplate.query(sql, new Object[]{region}, new CommonRowMapper());
        } catch (Exception e) {
            log.error("{}지역 전체 게시글 불러오기 실패{}", region, e.getMessage());
            return null;
        }

    }

    private static class CommonRowMapper implements RowMapper<PostVO> {
        @Override
        public PostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostVO(
                    rs.getString("POST_ID"),
                    rs.getString("POST_TITLE"),
                    rs.getInt("POST_PRICE"),
                    rs.getString("POST_REGION"),
                    rs.getString("IMG_URL"),
                    rs.getInt("POST_VIEW"),
                    getOffsetDateTime(rs.getTimestamp("POST_DATE"))
            );
        }
    }

    // 카테고리/지역 상관없이 조회수 상위 8개 게시물
    public List<PostVO> getTopEightPostList() {
        String sql = """
                SELECT P.POST_ID, P.POST_TITLE, P.POST_REGION, I.IMG_URL
                FROM (SELECT IMG_POST, IMG_URL
                      FROM POST_IMG
                      WHERE IMG_ID IN (SELECT IMG_ID
                                       FROM (SELECT IMG_ID,
                                                    ROW_NUMBER() OVER
                                                        (PARTITION BY IMG_POST
                                                        ORDER BY TO_NUMBER(SUBSTR(IMG_ID, 5))) AS rn
                                             FROM POST_IMG)
                                       WHERE rn = 1)) I
                         RIGHT JOIN (SELECT POST_ID,
                                      POST_TITLE,
                                      POST_REGION,
                                      POST_VIEW
                               FROM POST
                               WHERE POST_DISABLED = 0 AND ROWNUM <= 8
                               ORDER BY POST_VIEW DESC) P
                ON I.IMG_POST = P.POST_ID
                
                """;

        try {
            List<PostVO> lst = jdbcTemplate.query(sql, new MainSlickRowMapper());
            for (PostVO post : lst) {
                post.setPostRegion(getRegionName(post.getPostRegion()));
            }

            return lst;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class MainSlickRowMapper implements RowMapper<PostVO> {
        @Override
        public PostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostVO(
                    rs.getString("POST_ID"),
                    rs.getString("POST_TITLE"),
                    rs.getString("POST_REGION"),
                    rs.getString("IMG_URL")
            );
        }
    }

    // 검색어
    public List<PostVO> searchPostList(String region, String keyword) {
        // 공백으로 단어 분할
        String[] keywords = keyword.split("\\s+");
        StringBuilder likeClause = new StringBuilder();

        // 각 단어에 대해 LIKE 조건 생성
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) {
                likeClause.append(" OR ");
            }
            likeClause.append("P.POST_TITLE LIKE ?");
        }

        String sql = """
                SELECT P.POST_ID,
                       P.POST_TITLE,
                       P.POST_PRICE,
                       P.POST_REGION,
                       I.IMG_URL,
                       P.POST_VIEW,
                       P.POST_DATE
                FROM (SELECT IMG_POST, IMG_URL
                      FROM POST_IMG
                      WHERE IMG_ID IN (SELECT IMG_ID
                                       FROM (SELECT IMG_ID,
                                                    ROW_NUMBER() OVER
                                                    (PARTITION BY IMG_POST
                                                     ORDER BY TO_NUMBER(SUBSTR(IMG_ID, 5))) AS rn
                                             FROM POST_IMG)
                                       WHERE rn = 1)) I
                RIGHT JOIN (SELECT POST_ID,
                                   POST_TITLE,
                                   POST_PRICE,
                                   POST_REGION,
                                   POST_VIEW,
                                   POST_DATE
                            FROM POST
                            WHERE POST_REGION = ? AND POST_DISABLED = 0 AND (%s)) P
                ON P.POST_ID = I.IMG_POST
                """.formatted(likeClause.toString());

        // 파라미터 배열 생성
        Object[] params = new Object[keywords.length + 1];
        params[0] = region;
        for (int i = 0; i < keywords.length; i++) {
            params[i + 1] = "%" + keywords[i] + "%";
        }

        try {
            return jdbcTemplate.query(sql, params, new CommonRowMapper());
        } catch (Exception e) {
            log.error("{}, 검색 실패, {}", region, e.getMessage());
            return null;
        }
    }


}
