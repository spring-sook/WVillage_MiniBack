package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.RegionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class BaseDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
