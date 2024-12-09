package com.wvillage.wvillageJdbc.vo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor // 모든 매개변수가 있는 생성자
@NoArgsConstructor // 기본 생성자
@ToString
public class RegionVO {
    private String regionCode;
    private String regionSido;
    private String regionSigun;
    private String regionGu;
    private String regionEmd;
    private String regionRi;
}
