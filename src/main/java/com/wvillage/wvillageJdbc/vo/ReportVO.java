package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class ReportVO {
    private String reportId;
    private String reportReporter;
    private String reportReported;
    private String reportReason;
    private Date reportDate;
    private String reportContent;
}
