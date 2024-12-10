package com.wvillage.wvillageJdbc.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

@Slf4j
public class BaseDAO {

    private JdbcTemplate jdbcTemplate;
    // 지역코드를 이름으로 바꾸기
    public String getRegionName(String regionCode) {
        String sql = "SELECT REGION_SIDO, REGION_SIGUN, REGION_GU, REGION_EMD, REGION_RI FROM REGION WHERE REGION_CODE = ?";

        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, regionCode);

            // 각 열의 값을 가져와서 문자열로 결합
            String regionSido = (String) row.get("REGION_SIDO");
            String regionSigun = (String) row.get("REGION_SIGUN");
            String regionGu = (String) row.get("REGION_GU");
            String regionEmd = (String) row.get("REGION_EMD");
            String regionRI = (String) row.get("REGION_RI");

            StringBuilder regionName = new StringBuilder(regionSido);

            // 시/군 이 존재하는 경우 추가
            if (regionSigun != null) {
                regionName.append(" ").append(regionSigun);
            }

            // 구 추가
            if (regionGu != null) {
                regionName.append(" ").append(regionGu);
            }

            // 읍면동 추가
            if (regionEmd != null) {
                regionName.append(" ").append(regionEmd);
            }

            // 리 추가
            if (regionRI != null) {
                regionName.append(" ").append(regionRI);
            }

            return regionName.toString().trim();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
