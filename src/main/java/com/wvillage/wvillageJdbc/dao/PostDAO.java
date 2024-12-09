package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostDAO {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_POST_WRITE = "INSERT INTO POST (POST_EMAIL, POST_CATEGORY, " +
                                                        "POST_TITLE, POST_CONTENT, POST_PRICE, " +
                                                        "POST_REGION, POST_LOCATION) " +
                                                    "VALUES (?, ?, ?, ?, ?, ?, ?) ";
    private static final String GET_LAST_POSTID = "SELECT MAX(TO_NUMBER(REGEXP_SUBSTR(POST_ID, '\\d+$'))) " +
                                                    "FROM POST";
    private static final String INSERT_POST_IMG = "INSERT INTO POST_IMG (IMG_POST, IMG_URL) VALUES (?, ?) ";

    public String postWrite(PostVO postVo, List<String> imgUrls) {
        try {
            int result = jdbcTemplate.update(INSERT_POST_WRITE, postVo.getPostEmail(), postVo.getPostCat(),
                    postVo.getPostTitle(), postVo.getPostContent(), postVo.getPostPrice(),
                    postVo.getPostRegion(), postVo.getPostLocation());

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
}
