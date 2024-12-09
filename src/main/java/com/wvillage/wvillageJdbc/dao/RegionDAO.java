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
    private static final String GET_REGION = "SELECT REGION_SIDO, REGION_SIGUN, REGION_GU, REGION_EMD, REGION_RI " +
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
            region.setRegionSigun(rs.getString("REGION_SIGUN"));
            region.setRegionGu(rs.getString("REGION_GU"));
            region.setRegionEmd(rs.getString("REGION_EMD"));
            region.setRegionRi(rs.getString("REGION_RI"));
            return region;
        }
    }


}
