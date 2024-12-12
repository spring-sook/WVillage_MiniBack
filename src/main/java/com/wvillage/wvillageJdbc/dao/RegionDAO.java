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
    public List<RegionVO> filterSido() {
        String sql = """
                SELECT MIN(REGION_CODE) AS REGION_CODE, REGION_SIDO
                FROM REGION
                GROUP BY REGION_SIDO
                """;

        try {
            return jdbcTemplate.query(sql, new RegionNameMapper("REGION_SIDO"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


    // 지역 필터 검색
    public List<RegionVO> regionFilterList(String regionCode) {
        String sql = "", sido, sigungu, emd, ri;
        sido = regionCode.substring(0, 2);
        sigungu = regionCode.substring(2, 5);
        emd = regionCode.substring(5, 8);
        ri = regionCode.substring(8, 10);
        String newCode = regionCode.replaceAll("0+$", "");
        log.warn("원본 코드 : {}", regionCode);
        log.warn("요청 코드 : {}", newCode);


        try {
            log.error(sql + newCode);
            if (sigungu.equals("000") && !sido.equals("36")) {
                log.warn("세종시 제외 시군구 요청");
                sql = """
                        SELECT MIN(REGION_CODE) AS REGION_CODE, REGION_SIGUNGU
                        FROM REGION WHERE REGION_CODE LIKE ?
                                      AND REGION_SIGUNGU IS NOT NULL
                                      GROUP BY REGION_SIGUNGU""";
                return jdbcTemplate.query(sql, new Object[]{newCode + "%"}, new RegionNameMapper("REGION_SIGUNGU"));
            } else if (emd.equals("000")) {
                log.warn("읍면동 요청");
                sql = """
                        SELECT MIN(REGION_CODE) AS REGION_CODE, REGION_EMD
                        FROM REGION
                        WHERE REGION_CODE LIKE ?
                          AND REGION_EMD IS NOT NULL
                          GROUP BY REGION_EMD""";
                return jdbcTemplate.query(sql, new Object[]{newCode + "%"}, new RegionNameMapper("REGION_EMD"));
            } else if (ri.equals("00")) {
                log.warn("리 요청");
                sql = """
                        SELECT MIN(REGION_CODE) AS REGION_CODE, REGION_RI
                        FROM REGION
                        WHERE REGION_CODE LIKE ?
                          AND REGION_RI IS NOT NULL
                          GROUP BY REGION_RI""";
                return jdbcTemplate.query(sql, new Object[]{newCode + "%"}, new RegionNameMapper("REGION_RI"));
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }

    }

    private static class RegionNameMapper implements RowMapper<RegionVO> {

        private final String regionColumn;

        public RegionNameMapper(String regionColumn) {
            this.regionColumn = regionColumn;
        }

        @Override
        public RegionVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new RegionVO(
                    rs.getString("REGION_CODE"),
                    rs.getString(regionColumn)
            );
        }
    }

}
