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
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostListDAO {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public List<PostVO> getCommonAllPostList(String region) {
        String sql = "SELECT P.POST_ID,\n" +
                "       P.POST_TITLE,\n" +
                "       P.POST_PRICE,\n" +
                "       P.POST_REGION,\n" +
                "       I.IMG_URL\n" +
                "FROM (SELECT IMG_POST, IMG_URL\n" +
                "      FROM POST_IMG\n" +
                "      WHERE IMG_ID IN (SELECT MIN(IMG_ID)\n" +
                "                       FROM POST_IMG\n" +
                "                       GROUP BY IMG_POST)) I\n" +
                "         JOIN (SELECT POST_ID,\n" +
                "                      POST_TITLE,\n" +
                "                      POST_PRICE,\n" +
                "                      POST_REGION\n" +
                "               FROM POST\n" +
                "               WHERE POST_REGION = ? AND POST_DISABLED = 0) P\n" +
                "              ON P.POST_ID = I.IMG_POST";
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
                    rs.getString("IMG_URL")
            );
        }
    }
}
