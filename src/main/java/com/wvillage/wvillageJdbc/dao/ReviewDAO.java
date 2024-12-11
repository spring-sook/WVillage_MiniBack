package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReviewDAO {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    // 리뷰 작성하기
    @Transactional
    public boolean insertReview(String email, String reserve, String tags) {
        String reserveSql = "INSERT INTO REVIEW (REVIEW_EMAIL, REVIEW_RESERVE, REVIEW_TAG) VALUES (?, ?, ?)";
        String recordSql = "UPDATE REVIEW_RECORD SET REC_COUNT = REC_COUNT + 1 WHERE REC_EMAIL = ? AND REC_REVIEW = ?";
        String scoreSql = "SELECT SUM(TAG_SCORE) FROM REVIEW_TAG WHERE TAG_ID IN (%s)";
        String updateScoreSql = "UPDATE MEMBER SET SCORE = SCORE + ? WHERE EMAIL = ?";

        String[] tagList = tags.split(",");

        // 태그 ID를 문자열로 변환
        String tagIds = String.join(",", Arrays.stream(tagList)
                .map(String::trim)
                .map(tag -> "'" + tag + "'") // 각 태그를 문자열로 감쌈
                .toArray(String[]::new));

        try {
            jdbcTemplate.update(reserveSql, email, reserve, tags);

            for (String tag : tagList) {
                jdbcTemplate.update(recordSql, email, tag.trim()); // 공백 제거 후 태그 사용
            }

            log.warn("{} : {}", scoreSql, tagIds);
            int point = jdbcTemplate.queryForObject(String.format(scoreSql, tagIds), Integer.class);
            log.warn("포인트합계:{}", point);

            jdbcTemplate.update(updateScoreSql, point, email);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }


    // 게시글에 달린 리뷰 목록 반환
    public List<ReviewVO> getPostReviewList(String postId) {
        String sql = """
                SELECT M.NICKNAME, M.PROFILE_IMG, R.REVIEW_TAG
                FROM MEMBER M JOIN REVIEW R
                    ON M.EMAIL = R.REVIEW_EMAIL
                WHERE R.REVIEW_RESERVE IN (SELECT RES_ID 
                                           FROM RESERVE 
                                           WHERE RES_POST=?)""";
        List<ReviewVO> lst = null;
        try {
            lst = jdbcTemplate.query(sql, new Object[]{postId}, new postReviewListMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        List<ReviewVO> newList = new ArrayList<>();
        for (ReviewVO review : lst) {
            List<String> tagsList = tagsIntoString(review.getReviewTags()); // 태그 변환
            newList.add(new ReviewVO(review.getReviewEmail(), review.getReviewProfile(), tagsList)); // List<String>으로 변환하여 추가
        }

        return newList;
    }

    private static class postReviewListMapper implements RowMapper<ReviewVO> {

        @Override
        public ReviewVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ReviewVO(
                    rs.getString("NICKNAME"),
                    rs.getString("PROFILE_IMG"),
                    rs.getString("REVIEW_TAG")
            );
        }
    }


    // 태그 아이디를 내용으로 변환하기
    private List<String> tagsIntoString(String tags) {
        String sql = "SELECT TAG_ID, TAG_CONTENT FROM REVIEW_TAG";
        Map<String, String> tagsMap = new HashMap<>();

        try {
            List<ReviewVO> revList = jdbcTemplate.query(sql, new tagsContentRowMapper());
            for (ReviewVO review : revList) {
                tagsMap.put(review.getReviewId(), review.getReviewContent());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        List<String> tagsList = new ArrayList<>(Arrays.asList(tags.split(",")));
        List<String> resultList = new ArrayList<>();

        for (String tag : tagsList) {
            String content = tagsMap.get(tag.trim()); // 공백 제거 후 키로 사용
            if (content != null) {
                resultList.add(content);
            } else {
                resultList.add("Unknown Tag"); // 존재하지 않는 태그에 대한 처리
            }
        }

        return resultList;
    }

    private static class tagsContentRowMapper implements RowMapper<ReviewVO> {
        @Override
        public ReviewVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ReviewVO(
                    rs.getString("TAG_ID"),
                    rs.getString("TAG_CONTENT")
            );
        }
    }

    // 해당 사용자의 이메일을 인자로 받아 VO의 리스트를 반환
    public List<ReviewVO> getReviewRecord(String email) {
        String sql = "SELECT RR.REC_EMAIL, RT.TAG_CONTENT, RR.REC_COUNT " +
                "FROM (SELECT * FROM REVIEW_RECORD WHERE REC_EMAIL = ?) RR " +
                "JOIN REVIEW_TAG RT ON RR.REC_REVIEW = RT.TAG_ID";

        try {
            return jdbcTemplate.query(sql, new Object[]{email}, new ReviewRecordMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class ReviewRecordMapper implements RowMapper<ReviewVO> {
        @Override
        public ReviewVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ReviewVO(
                    rs.getString("REC_EMAIL"),
                    rs.getString("TAG_CONTENT"),
                    rs.getInt("REC_COUNT")
            );
        }
    }
}
