package com.wvillage.wvillageJdbc.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
