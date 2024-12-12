package com.wvillage.wvillageJdbc.vo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class CommonVo {
    private AccountVO account;
    private BookmarkVO bookmark;
    private MemberVO member;
    private PostVO post;
    private RegionVO region;
    private ReportVO report;
    private ReserveVO reserve;
    private ReviewVO review;

    public CommonVo(PostVO post, ReserveVO reserve, ReviewVO review) {
        this.post = post;
        this.reserve = reserve;
        this.review = review;
    }
}
