package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.RegionVO;
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
public class RegionDAO {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_REGION = "SELECT REGION_SIDO, REGION_SIGUNGU, REGION_EMD, REGION_RI " +
                                             "FROM REGION WHERE REGION_CODE = ? ";

    public List<RegionVO> getRegion(String regionCode) {
        try {
            return jdbcTemplate.query(GET_REGION, new Object[]{regionCode}, new regionRowMapper());
        } catch (DataAccessException e) {
            log.error("지역명 불러오는 중 오류", e);
            throw e;
        }
    }

    private static class regionRowMapper implements RowMapper<RegionVO> {
        @Override
        public RegionVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            RegionVO region = new RegionVO();
            region.setRegionSido(rs.getString("REGION_SIDO"));
            region.setRegionSiGunGu(rs.getString("REGION_SIGUNGU"));
            region.setRegionEmd(rs.getString("REGION_EMD"));
            region.setRegionRi(rs.getString("REGION_RI"));
            return region;
        }
    }

    // 시도 이름 리스트
    public List<RegionVO> filterSido(){
        String sql = """
                SELECT MIN(REGION_CODE) AS RE_CODE, REGION_SIDO
                FROM REGION
                GROUP BY REGION_SIDO
                """;

        try{
            return jdbcTemplate.query(sql, new SidoRowMapper());
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    private static class SidoRowMapper implements RowMapper<RegionVO> {

        @Override
        public RegionVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new RegionVO(
                    rs.getString("RE_CODE"),
                    rs.getString("REGION_SIDO")
            );
        }
    }

//    public List<RegionVO> regionFilterList(String regionCode) {
//        String sql, sido, sigun, gu, emd, ri;
//        sido = regionCode.substring(0, 2);
//        sigun = regionCode.substring(2, 4);
//        gu = regionCode.substring(4, 5);
//        emd = regionCode.substring(5, 8);
//        ri = regionCode.substring(8, 11);
//
//        if(sigun.equals("00") && !sido.equals("36")) {
//            sql = "SELECT REGION_CODE, REGION_SIGUN FROM REGION WHERE REGION_CODE = '?%' AND REGION_SIGUN IS NOT NULL";
//        }else if(sido.equals("36")){
//            sql = "SELECT REGION_CODE, REGION_EMD FROM REGION WHERE REGION_CODE = '?%'";
//        }
//    }

//    private static boolean isRegionEmpty(List<RegionVO> lst) {
//        for(RegionVO region : lst) {
//            if(region != null) return false;
//        }
//
//        return true;
//    }

}
