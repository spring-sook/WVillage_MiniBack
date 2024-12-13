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
    private String reporterEmail;
    private String reporterNickName;
    private String reporterProfileImg;
    private int reporterCount;
    private String reportedEmail;
    private String reportedNickName;
    private String reportedProfileImg;
    private int reportedCount;
}
