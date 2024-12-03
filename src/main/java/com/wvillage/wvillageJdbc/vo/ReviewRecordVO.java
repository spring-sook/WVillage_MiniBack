package com.wvillage.wvillageJdbc.vo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@RequiredArgsConstructor
@ToString
public class ReviewRecordVO {
    private String recordEmail; //
    private String recordReview;
    private int recordCount;
}
