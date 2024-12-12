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
public class BookmarkDAO extends BaseDAO {

    private final JdbcTemplate jdbcTemplate;

    public int isBookmarking(String email, String postId){
        String sql = "SELECT COUNT(*) FROM bookmark WHERE BK_EMAIL = ? AND BK_POST = ?";

        try{
            return jdbcTemplate.queryForObject(sql, new Object[]{email, postId}, Integer.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Boolean insertBookmark(String postId, String email) {
        String sql = "INSERT INTO BOOKMARK (BK_POST, BK_EMAIL) VALUES (?, ?) ";

        try {
            int result = jdbcTemplate.update(sql, postId, email);
            return result > 0;
        } catch (DataAccessException e) {
            log.error("북마크 삽입 중 에러 발생", e);
            return false;
        }
    }

    public Boolean deleteBookmark(String postId, String email) {
        String sql = "DELETE FROM BOOKMARK WHERE BK_POST = ? AND BK_EMAIL = ? ";

        try {
            int result = jdbcTemplate.update(sql, postId, email);
            return result > 0;
        } catch (DataAccessException e) {
            log.error("북마크 삭제 중 에러 발생", e);
            return false;
        }
    }

    public List<PostVO> getBookmarkedPostList(String email) {
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
                                 WHERE POST_ID IN (SELECT BK_POST
                                                   FROM BOOKMARK
                                                   WHERE BK_EMAIL = ? )
                                   AND POST_DISABLED = 0) P
                        ON P.POST_ID = I.IMG_POST
                """;
        try {
            return jdbcTemplate.query(sql, new Object[]{email}, new GetBookmarkedPost());
        } catch (DataAccessException e) {
            log.error("북마크된 게시글 가져오기 실패", e);
            return null;
        }
    }

    private static class GetBookmarkedPost implements RowMapper<PostVO> {
        @Override
        public PostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostVO(
                    rs.getString("POST_ID"),
                    rs.getString("POST_TITLE"),
                    rs.getInt("POST_PRICE"),
                    rs.getString("POST_REGION"),
                    rs.getString("IMG_URL"),
                    rs.getInt("POST_VIEW"),
                    rs.getTimestamp("POST_DATE").toLocalDateTime().format(formatter)
            );
        }
    }
}
