package com.wvillage.wvillageJdbc.dao;

import com.wvillage.wvillageJdbc.vo.ReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReportDAO extends BaseDAO {

    private final JdbcTemplate jdbcTemplate;

    // 신고 리스트 대기, 승인, 거절로 반환
    public List<ReportVO> getReportList() {
        String sql = """
                SELECT
                    r.REPORT_ID,
                    r.REPORT_CONTENT,
                    r.REPORT_DATE,
                    r.REPORT_STATE,
                    reported.NICKNAME AS REPORTED_NICKNAME,
                    reported.EMAIL AS REPORTED_EMAIL,
                    reported.PROFILE_IMG AS REPORTED_PROFILE_IMG,
                    reporter.NICKNAME AS REPORTER_NICKNAME,
                    reporter.EMAIL AS REPORTER_EMAIL,
                    reporter.PROFILE_IMG AS REPORTER_PROFILE_IMG,
                    SUM(CASE WHEN r.REPORT_STATE = 'accept' THEN 1 ELSE 0 END) OVER ( PARTITION BY r.REPORT_REPORTED) AS REPORTED_COUNT,
                    COUNT(r.REPORT_REPORTER) OVER (PARTITION BY r.REPORT_REPORTER) AS REPORTER_COUNT
                FROM REPORT r
                LEFT JOIN MEMBER reported ON r.REPORT_REPORTED = reported.EMAIL
                LEFT JOIN MEMBER reporter ON r.REPORT_REPORTER = reporter.EMAIL
                ORDER BY
                    CASE
                       WHEN r.REPORT_STATE = 'wait' THEN 1
                       WHEN r.REPORT_STATE = 'accept' THEN 2
                       WHEN r.REPORT_STATE = 'deny' THEN 3
                       ELSE 4
                    END
                """;

        try {
            return jdbcTemplate.query(sql, new ReportListRowMapper());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static class ReportListRowMapper implements RowMapper<ReportVO> {

        @Override
        public ReportVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ReportVO(
                    rs.getString("REPORT_ID"),
                    rs.getString("REPORT_CONTENT"),
                    getOffsetDateTime(rs.getTimestamp("REPORT_DATE")),
                    rs.getString("REPORT_STATE"),
                    rs.getString("REPORTER_EMAIL"),
                    rs.getString("REPORTER_NICKNAME"),
                    rs.getString("REPORTER_PROFILE_IMG"),
                    rs.getInt("REPORTER_COUNT"),
                    rs.getString("REPORTED_EMAIL"),
                    rs.getString("REPORTED_NICKNAME"),
                    rs.getString("REPORTED_PROFILE_IMG"),
                    rs.getInt("REPORTED_COUNT")
            );
        }
    }

    public boolean insertReport(ReportVO report) {
        String sql = """
                INSERT INTO REPORT (REPORT_REPORTER, REPORT_REPORTED, REPORT_CONTENT)
                VALUES (?, ?, ?)
                """;

        try {
            int row = jdbcTemplate.update(sql, report.getReporterEmail(), report.getReportedEmail(), report.getReportContent());
            return row > 0;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean updateReport(ReportVO vo) {
        String approve = """
                UPDATE REPORT SET
                REPORT_STATE = 'accept'
                WHERE REPORT_ID = ?
                """;

        String score = """
                UPDATE MEMBER SET
                SCORE = SCORE - 10
                WHERE EMAIL = ?
                """;

        String deny = """
                UPDATE REPORT SET
                REPORT_STATE = 'deny'
                WHERE REPORT_ID = ?
                """;

        try {
            int row = 0;
            if (vo.getReportState().equals("accept")) {
                row += jdbcTemplate.update(approve, vo.getReportId());
                row += jdbcTemplate.update(score, vo.getReportedEmail());
            } else {
                row += jdbcTemplate.update(deny, vo.getReportId());
            }

            return row > 0;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

    }


}
