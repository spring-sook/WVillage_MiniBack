package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class ReviewVO {
    private String reviewEmail; //
    private String reviewContent;
    private List<String> reviewTags;
    private int recordCount;

    public ReviewVO(String reviewEmail, String reviewContent, int recordCount) {
        this.reviewEmail = reviewEmail;
        this.reviewContent = reviewContent;
        this.recordCount = recordCount;
    }
}
