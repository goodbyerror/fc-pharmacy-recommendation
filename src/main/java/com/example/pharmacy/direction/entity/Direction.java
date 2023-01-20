package com.example.pharmacy.direction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name= "direction")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Direction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //고객
    private String inputAddress;
    private double inputLatitude;
    private double inputLongitude;

    //약국
    private String targetPharmacyName;
    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    //약국과 고객 위치의 거리
    private double distance;
}
