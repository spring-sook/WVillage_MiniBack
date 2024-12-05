package com.wvillage.wvillageJdbc.vo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class MemberVO {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private int score;
    private String profileImg;
    private int exist;
    private String areaCode;
    private String tier;
    private int point;
    private String signupDate;
    private String signoutDate;
    private int reportCount;
}
