package com.example.pharmacy.pharmacy.service;

import com.example.pharmacy.api.dto.DocumentDto;
import com.example.pharmacy.api.dto.KakaoApiResponseDto;
import com.example.pharmacy.api.service.KakaoAddressSearchService;
import com.example.pharmacy.direction.dto.OutputDto;
import com.example.pharmacy.direction.entity.Direction;
import com.example.pharmacy.direction.service.Base62Service;
import com.example.pharmacy.direction.service.DirectionService;
import com.example.pharmacy.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;
    private final PharmacyRepository pharmacyRepository;
    private final Base62Service base62Service;

    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";

    @Value("${pharmacy.recommendation.base.url}")
    private String baseUrl;

    public List<OutputDto> recommendationPharmacyList(String address) {

        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentDtoList())) {
            log.error("[PharmacyRecommendationService recommendationPharmacyList fail] Input address {} ", address);
            return Collections.emptyList();
        }

        DocumentDto documentDto = kakaoApiResponseDto.getDocumentDtoList().get(0);
        List<Direction> directions = directionService.buildDirectionListByCategoryApi(documentDto);

        List<Direction> directionList = directionService.saveAll(directions);

        return directionList.stream()
                .map(this::convertToOutputDto)
                .collect(Collectors.toList());
    }

    private OutputDto convertToOutputDto(Direction direction) {
        return OutputDto.builder()
                .pharmacyAddress(direction.getTargetAddress())
                .pharmacyName(direction.getTargetPharmacyName())
                .directionUrl(baseUrl + base62Service.encodeDirectionId(direction.getId()))
                .roadViewUrl(ROAD_VIEW_BASE_URL + direction.getTargetLatitude() + "," + direction.getTargetLongitude())
                .distance(String.format("%.2f km", direction.getDistance()))
                .build();

    }
}
