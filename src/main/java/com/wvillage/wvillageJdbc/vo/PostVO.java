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
    private int bookmarked;
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

    public PostVO(String postId, String postTitle, int postPrice, String postRegion, String postThumbnail, int postViews, Date postDate) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.postRegion = postRegion;
        this.postThumbnail = postThumbnail;
        this.postViews = postViews;
        this.postDate = postDate;
    }

    //
    public PostVO(String postId, String postTitle, int postPrice, String postRegion, String postThumbnail, boolean postEnable) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.postRegion = postRegion;
        this.postThumbnail = postThumbnail;
        this.postEnable = postEnable;
    }

    public PostVO(String postEmail, String postCat, String postTitle, String postContent, int postPrice, String postRegion, String postLocation) {
        this.postEmail = postEmail;
        this.postCat = postCat;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postPrice = postPrice;
        this.postRegion = postRegion;
        this.postLocation = postLocation;
    }

    // 게시글 내용
    public PostVO(String postId, String postTitle, int postViews,int postPrice, int postDeal, int bookmarked,String postRegion, String postLocation, String postContent){
        this.postId = postId;
        this.postTitle = postTitle;
        this.postViews = postViews;
        this.postPrice = postPrice;
        this.postDeal = postDeal;
        this.bookmarked = bookmarked;
        this.postRegion = postRegion;
        this.postLocation = postLocation;
        this.postContent = postContent;
    }

    // 메인화면 슬릭
    public PostVO(String postId, String postTitle, String postRegion, String postThumbnail){
        this.postId = postId;
        this.postTitle = postTitle;
        this.postRegion = postRegion;
        this.postThumbnail = postThumbnail;
    }
}
