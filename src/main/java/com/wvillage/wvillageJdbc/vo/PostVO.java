package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class PostVO {
    private String postId;
    private String postEmail;
    private String postCat;
    private Date postDate;
    private String postTitle;
    private String postContent;
    private List<String> postImg;
    private String postThumbnail;
    private int postViews;
    private int postPrice;
    private String postRegion;
    private int postDeal;
    private boolean postEnable;
    private String postLocation;  //상세위치

    // 일반 조회 목록에서
    public PostVO(String postId, String postCat, String postTitle, int postPrice, String postRegion, boolean postEnable) {
        this.postId = postId;
        this.postCat = postCat;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.postRegion = postRegion;
        this.postEnable = postEnable;
    }

    public PostVO(String postId, String postTitle, int postPrice, String postRegion, String postThumbnail) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.postRegion = postRegion;
        this.postThumbnail = postThumbnail;
    }
}
