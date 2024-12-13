package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.lang.reflect.Member;

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
    private String grade;
    private int point;
    private String signupDate;
    private String signoutDate;
    private int reportCount;


    public MemberVO(String email, String nickname, String profileImg, int score, int reportCount) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.score = score;
        this.reportCount = reportCount;
    }

    public MemberVO(String email, String name, String nickname, String phone, int score, String profileImg, String areaCode, String grade, int point) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.score = score;
        this.profileImg = profileImg;
        this.areaCode = areaCode;
        this.grade = grade;
        this.point = point;
    }

    // 게시글을 올린 유저의 정보
    public MemberVO(String email, String nickname, String profileImg, String areaCode, int point) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.areaCode = areaCode;
        this.point = point;
    }

    public MemberVO(String email, String areaCode) {
        this.email = email;
        this.areaCode = areaCode;
    }

}


