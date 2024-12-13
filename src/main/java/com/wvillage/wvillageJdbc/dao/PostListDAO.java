package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
                       P.POST_DISABLED,
                       P.POST_LOCATION
                FROM (SELECT IMG_POST, IMG_URL
                  FROM POST_IMG
                  WHERE IMG_ID IN (SELECT IMG_ID
                                   FROM (SELECT IMG_ID,
                                                ROW_NUMBER() OVER (PARTITION BY IMG_POST
                                                                    ORDER BY SUBSTR(IMG_ID, 5) + 0) AS rn
                                         FROM POST_IMG)
                                   WHERE rn = 1)) I
                          RIGHT JOIN (SELECT POST_ID,
                                      POST_TITLE,
                                      POST_PRICE,
                                      POST_REGION,
                                      POST_DISABLED,
                                      POST_LOCATION
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

    // 검색어
    public List<PostVO> getPostList(String region, String category, String keyword) {
        List<String> paramsList = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder("POST_REGION LIKE ? AND POST_DISABLED = 0");
        String newCode = region.replaceAll("0+$", "");
        paramsList.add("%" + newCode + "%");

        if (category != null && !category.isBlank()) {
            whereClause.append(" AND POST_CATEGORY = ?");
            paramsList.add(category);
        }

        if (keyword != null && !keyword.isBlank()) {
            String[] keywords = keyword.split("\\s+");
            whereClause.append(" AND (");
            for (int i = 0; i < keywords.length; i++) {
                if (i > 0) {
                    whereClause.append(" OR ");
                }
                whereClause.append("P.POST_TITLE LIKE ?");
                paramsList.add("%" + keywords[i] + "%");
            }
            whereClause.append(")");
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
                                                    ROW_NUMBER() OVER (PARTITION BY IMG_POST ORDER BY SUBSTR(IMG_ID, 5) + 0) AS rn
                                             FROM POST_IMG)
                                       WHERE rn = 1)) I
                LEFT JOIN POST P ON P.POST_ID = I.IMG_POST
                WHERE %s
                """.formatted(whereClause.toString());

        try {
            return jdbcTemplate.query(sql, paramsList.toArray(), new CommonRowMapper());
        } catch (Exception e) {
            log.error("게시글 목록 불러오기 실패, region: {}, category: {}, keyword: {}, error: {}", getRegionName(region), category, keyword, e.getMessage());
            return null;
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
                                                ROW_NUMBER() OVER (PARTITION BY IMG_POST ORDER BY SUBSTR(IMG_ID, 5) + 0) AS rn
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
            return jdbcTemplate.query(sql, new MainSlickRowMapper());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class MainSlickRowMapper implements RowMapper<PostVO> {
        @Override
        public PostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostVO(
                    rs.getString("POST_ID"),
                    rs.getString("POST_TITLE"),
                    getRegionName(rs.getString("POST_REGION")),
                    rs.getString("IMG_URL")
            );
        }
    }

    private class UserPostlistRowMapper implements RowMapper<PostVO> {
        @Override
        public PostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostVO(
                    rs.getString("POST_ID"),
                    rs.getString("POST_TITLE"),
                    rs.getInt("POST_PRICE"),
                    getRegionName(rs.getString("POST_REGION")),
                    rs.getString("POST_LOCATION"),
                    rs.getString("IMG_URL"),
                    rs.getBoolean("POST_DISABLED")
            );
        }
    }

    private class CommonRowMapper implements RowMapper<PostVO> {
        @Override
        public PostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostVO(
                    rs.getString("POST_ID"),
                    rs.getString("POST_TITLE"),
                    rs.getInt("POST_PRICE"),
                    getRegionName(rs.getString("POST_REGION")),
                    rs.getString("IMG_URL"),
                    rs.getInt("POST_VIEW"),
                    getOffsetDateTime(rs.getTimestamp("POST_DATE"))
            );
        }
    }


}
