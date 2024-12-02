package com.wvillage.wvillageJdbc.vo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class UserProfileVo {
    private String email;
    private String nickName;
    private String profileImg;

}
