package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class ReviewVO {
    private String reviewId;
    private String reviewEmail; //
    private String reviewProfile;
    private String reviewContent;
    private String reviewTags;
    private List<String> reviewTagContent;
    private int recordCount;


    // 특정 유저가 작성한 리뷰 목록을 위한 생성자
    public ReviewVO(String reviewEmail, String reviewContent, int recordCount) {
        this.reviewEmail = reviewEmail;
        this.reviewContent = reviewContent;
        this.recordCount = recordCount;
    }

    public ReviewVO(String reviewEmail, String reviewProfile, String reviewTags){
        this.reviewEmail = reviewEmail;
        this.reviewProfile = reviewProfile;
        this.reviewTags = reviewTags;
    }

    public ReviewVO(String reviewEmail, String reviewContent, List<String> reviewTagContent){
        this.reviewEmail = reviewEmail;
        this.reviewContent = reviewContent;
        this.reviewTagContent = reviewTagContent;
    }

    // 리뷰 태그의 내용을 받아오기 위한 생성자
    public ReviewVO(String reviewId, String reviewContent){
        this.reviewId = reviewId;
        this.reviewContent = reviewContent;
    }


}
