package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.util.Date;

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
    // private String postImg;
    private int postViews;
    private int postDailyAmount;
    private int postHourlyAmount;
    private int postRegion;
    private int postDeal;
    private boolean postEnable;

}
