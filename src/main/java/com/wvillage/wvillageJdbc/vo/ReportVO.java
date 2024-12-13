package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class ReportVO {
    private String reportId;
    private String reportContent;
    private OffsetDateTime reportDate;
    private String reportState;
    private String reporter;
    private String reporterNickName;
    private String reporterImg;
    private int reporterCount;
    private String reported;
    private String reportedNickName;
    private String reportedImg;
    private int reportedCount;
}
