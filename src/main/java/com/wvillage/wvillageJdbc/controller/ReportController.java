package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.ReportDAO;
import com.wvillage.wvillageJdbc.vo.ReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/report")

public class ReportController {

    private final ReportDAO reportDAO;

    // 신고 리스트 전체 반환
    @GetMapping("/reportList")
    public ResponseEntity<List<ReportVO>> getReportList() {
        List<ReportVO> reportList = reportDAO.getReportList();
        return ResponseEntity.ok(reportList);
    }

    // 신고하기
    @PostMapping("/insertReport")
    public ResponseEntity<Boolean> insertReport(@RequestBody ReportVO reportVO) {
        boolean status = reportDAO.insertReport(reportVO);
        return ResponseEntity.ok(status);
    }

    // 신고 승인/거절
    @PostMapping("/updateReport")
    public ResponseEntity<Boolean> updateReport(@RequestBody ReportVO reportVO) {
        boolean status = reportDAO.updateReport(reportVO);
        return ResponseEntity.ok(status);
    }
}
