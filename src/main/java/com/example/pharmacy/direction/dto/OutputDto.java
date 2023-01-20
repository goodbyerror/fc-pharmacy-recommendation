package com.example.pharmacy.direction.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutputDto {

    private String pharmacyName;    //약국명
    private String pharmacyAddress; //약국주소
    private String directionUrl;    //길안내 url
    private String roadViewUrl;     //로드뷰 rul
    private String distance;        //고객주소와 실제 약국 주소와의 거리
}
