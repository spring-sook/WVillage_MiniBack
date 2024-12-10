package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostDAO {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_POST_WRITE = "INSERT INTO POST (POST_EMAIL, POST_CATEGORY, " + "POST_TITLE, POST_CONTENT, POST_PRICE, " + "POST_REGION, POST_LOCATION) " + "VALUES (?, ?, ?, ?, ?, ?, ?) ";
    private static final String GET_LAST_POSTID = "SELECT MAX(TO_NUMBER(REGEXP_SUBSTR(POST_ID, '\\d+$'))) " + "FROM POST";
    private static final String INSERT_POST_IMG = "INSERT INTO POST_IMG (IMG_POST, IMG_URL) VALUES (?, ?) ";

    public String postWrite(PostVO postVo, List<String> imgUrls) {
        try {
            int result = jdbcTemplate.update(INSERT_POST_WRITE, postVo.getPostEmail(), postVo.getPostCat(), postVo.getPostTitle(), postVo.getPostContent(), postVo.getPostPrice(), postVo.getPostRegion(), postVo.getPostLocation());

            if (result > 0) {

                String postId = jdbcTemplate.queryForObject(GET_LAST_POSTID, String.class);

                for (String imgUrl : imgUrls) {
                    log.error(imgUrl);
                    jdbcTemplate.update(INSERT_POST_IMG, "POST_" + postId, imgUrl);
                }
                return postId;
            }
            return null;
        } catch (DataAccessException e) {
            log.error("게시글 작성 중 예외 발생", e);
            return null;
        }
    }


    // 이미지 목록 반환
    public List<String> getImgUrls(String postId) {
        String sql = "SELECT IMG_URL FROM POST_IMG WHERE IMG_POST = ? ";

        try {
            return jdbcTemplate.queryForList(sql, new Object[]{postId}, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // 게시글 세부 내용 반환
//    public PostVO getPostDetail(String postId) {
//        String sql = """
//                SELECT POST_ID, POST_TITLE,POST_VIEW, POST_PRICE, POST_CONTENT, POST_LOCATION, COUNT(*) AS POST_BK
//                FROM BOOKMARK
//                RIGHT JOIN (SELECT * FROM POST WHERE POST_ID = ?);
//                """;
//    }

    private static class DeatailRowMapper implements RowMapper<PostVO> {

        @Override
        public PostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostVO(
                    rs.getString("POST_ID"),
                    rs.getString("POST_TITLE"),
                    rs.getInt("POST_VIEW"),
                    rs.getInt("POST_PRICE"),
                    rs.getString("POST_CONTENT"),
                    rs.getString("POST_LOCATION"),
                    rs.getInt("POST_BK")
            );
        }
    }
}
