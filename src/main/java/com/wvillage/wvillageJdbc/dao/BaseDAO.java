package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.RegionVO;
import com.wvillage.wvillageJdbc.vo.ReviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BaseDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 지역코드를 이름으로 바꾸기
    public String getRegionName(String regionCode) {
        String sql = "SELECT REGION_SIDO, REGION_SIGUNGU, REGION_EMD, REGION_RI FROM REGION WHERE REGION_CODE = ?";

        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, regionCode);

            // 각 열의 값을 가져와서 문자열로 결합
            String regionSido = (String) row.get("REGION_SIDO");
            String regionSiGunGu = (String) row.get("REGION_SIGUNGU");
            String regionEmd = (String) row.get("REGION_EMD");
            String regionRi = (String) row.get("REGION_RI");

            StringBuilder regionName = new StringBuilder(regionSido);

            // 시/군 이 존재하는 경우 추가
            if (regionSiGunGu != null) {
                regionName.append(" ").append(regionSiGunGu);
            }

            // 읍면동 추가
            if (regionEmd != null) {
                regionName.append(" ").append(regionEmd);
            }

            // 리 추가
            if (regionRi != null) {
                regionName.append(" ").append(regionRi);
            }

            return regionName.toString().trim();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // 태그 아이디를 내용으로 변환하기
    public List<String> tagsIntoString(String tags) {
        String sql = "SELECT TAG_ID, TAG_CONTENT FROM REVIEW_TAG";
        Map<String, String> tagsMap = new HashMap<>();

        if (tags == null) {
            return null;
        }

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

    public static OffsetDateTime getOffsetDateTime(Timestamp time) {
        return time.toInstant().atZone(ZoneId.of("Asia/Seoul")).toOffsetDateTime();
    }


    // 태그 아이디를 내용으로 변환하기
    public List<ReviewVO> tagsIntoVo(String tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList(); // null 또는 빈 문자열인 경우 빈 리스트 반환
        }

        List<String> tagsList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty()) // 빈 태그 제거
                .toList();

        if (tagsList.isEmpty()) {
            return Collections.emptyList();
        }

        String inClause = String.join(",", Collections.nCopies(tagsList.size(), "?"));
        String sql = String.format("SELECT TAG_ID, TAG_CONTENT, TAG_SCORE FROM REVIEW_TAG WHERE TAG_ID IN (%s)", inClause);

        try {
            List<ReviewVO> reviewVOList = jdbcTemplate.query(sql, tagsList.toArray(), (rs, rowNum) -> new ReviewVO(
                    rs.getString("TAG_CONTENT"),
                    rs.getInt("TAG_SCORE")));
            return reviewVOList;
        } catch (Exception e) {
            log.error("태그 조회 실패: {}", e.getMessage());
            return Collections.emptyList(); // 예외 발생 시 빈 리스트 반환
        }
    }
}
