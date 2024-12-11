package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class ReserveVO {
    private String reserveId;
    private String reservePost;
    private String reserveEmail;
    private Date reserveStart;
    private Date reserveEnd;
    private String reserveState;
    private boolean reserveMagRead;
    private String reserveReason; // 거부/취소 사유

    public ReserveVO(Date reserveStart, Date reserveEnd) {
        this.reserveStart = reserveStart;
        this.reserveEnd = reserveEnd;
    }
}
