package com.wvillage.wvillageJdbc.vo;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class ReserveVO {
    private String reserveId;
    private String reservePost;
    private String reserveEmail;
    private OffsetDateTime reserveStart;
    private OffsetDateTime reserveEnd;
    private int reserveTotalPrice;
    private String reserveState;
    private boolean reserveNewMsg;
    private String reserveReason; // 거부/취소 사유

    public ReserveVO(OffsetDateTime reserveStart, OffsetDateTime reserveEnd) {
        this.reserveStart = reserveStart;
        this.reserveEnd = reserveEnd;
    }

    public ReserveVO(String reserveId, OffsetDateTime reserveStart, OffsetDateTime reserveEnd, String reserveState, String reserveReason) {
        this.reserveId = reserveId;
        this.reserveStart = reserveStart;
        this.reserveEnd = reserveEnd;
        this.reserveState = reserveState;
        this.reserveReason = reserveReason;
    }
}
