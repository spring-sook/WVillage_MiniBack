package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.RegionVO;
import com.wvillage.wvillageJdbc.vo.ReviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

        if(tags == null) {
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

//    public List<String> searchRegion(String regionCode) {
//        String sidoSql = "SELECT REGION_CODE, REGION_SIDO FROM REGION WHERE REGION_CODE LIKE '?%'";
//        String sigunSql = "SELECT REGION_CODE, REGION_SIGUN FROM REGION WHERE REGION_CODE LIKE '?%'";
//        String guSql = "SELECT REGION_CODE, REGION_GU FROM REGION WHERE REGION_CODE LIKE '?%'";
//        String emdSql = "SELECT REGION_CODE, REGION_EMD FROM REGION WHERE REGION_CODE LIKE '?%'";
//        String riSql = "SELECT REGION_CODE, REGION_RI FROM REGION WHERE REGION_CODE LIKE '?%'";
//
//        try{
//
//        } catch (RuntimeException e) {
//            log.error(e.getMessage());
//        }
//    }

//    private static String isEmpty(List<RegionVO> lst) {
//        for(RegionVO vo : lst) {
//            if(vo.getRegionSigun() != null) {
//                return "sigun";
//            } else if(vo.getRegionGu() != null) {
//                return "gu";
//            }else if(vo.getRegionEmd() != null) {
//                return "emd";
//            }
//        }
//    }
}
