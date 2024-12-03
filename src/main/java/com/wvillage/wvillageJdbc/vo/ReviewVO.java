package com.wvillage.wvillageJdbc.vo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class ReviewVO {
    private String reviewId;
    private String reviewEmail;
    private String reviewPost;
    private String reviewTags;
}
