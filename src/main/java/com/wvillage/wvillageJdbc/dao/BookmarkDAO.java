package com.wvillage.wvillageJdbc.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BookmarkDAO {

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
}
