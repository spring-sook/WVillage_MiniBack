package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostDAO {
    private static final JdbcTemplate jdbcTemplate = null;
    private static final String INSERT_POST_WRITE = "INSERT INTO POST (POST_EMAIL, POST_CATEGORY, " +
                                                        "POST_TITLE, POST_CONTENT, POST_PRICE, " +
                                                        "POST_REGION, POST_LOCATION) " +
                                                    "VALUES (?, ?, ?, ?, ?, ?, ?) ";

    public static boolean postWrite(PostVO postVo) {
        try {
            int result = jdbcTemplate.update(INSERT_POST_WRITE, postVo.getPostEmail(), postVo.getPostCat(),
                    postVo.getPostTitle(), postVo.getPostContent(), postVo.getPostPrice(),
                    postVo.getPostRegion(), postVo.getPostLocation());
            return result > 0;
        } catch (DataAccessException e) {
            log.error("게시글 작성 중 예외 발생", e);
            return false;
        }
    }
}
