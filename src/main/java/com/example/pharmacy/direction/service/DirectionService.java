package com.example.pharmacy.direction.service;

import com.example.pharmacy.api.dto.DocumentDto;
import com.example.pharmacy.api.service.KakaoCategorySearchService;
import com.example.pharmacy.direction.entity.Direction;
import com.example.pharmacy.direction.repository.DirectionRepository;
import com.example.pharmacy.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {
    private static final int MAX_SEARCH_COUNT = 3;
    private static final double RADIUS_KM = 10.0;

    private final PharmacySearchService pharmacySearchService;
    private final DirectionRepository directionRepository;

    private final KakaoCategorySearchService kakaoCategorySearchService;

    private final Base62Service base62Service;

    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";

    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {
        if (CollectionUtils.isEmpty(directionList)) return Collections.emptyList();
        return directionRepository.saveAll(directionList);
    }

    public String findDirectionById(String encodedDirectionId) {
        Long decodedId = base62Service.decodeDirectionId(encodedDirectionId);
        Direction direction = directionRepository.findById(decodedId).orElse(null);

        String params = String.join(",", direction.getTargetPharmacyName(),
                String.valueOf(direction.getTargetLatitude()),
                String.valueOf(direction.getTargetLongitude()));

        String result = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params)
                .toUriString();

        return result;
    }

    public List<Direction> buildDirectionList(DocumentDto documentDto) {
        if (Objects.isNull(documentDto)) return Collections.emptyList();
        //약국 데이터 조회
        return pharmacySearchService.searchPharmacyDtoList().stream()
                .map(pharmacyDto ->
                        Direction.builder()
                                .inputAddress(documentDto.getAddressName())
                                .inputLatitude(documentDto.getLatitude())
                                .inputLongitude(documentDto.getLongitude())
                                .targetAddress(pharmacyDto.getPharmacyAddress())
                                .targetPharmacyName(pharmacyDto.getPharmacyName())
                                .targetLatitude(pharmacyDto.getLatitude())
                                .targetLongitude(pharmacyDto.getLongitude())
                                .distance(
                                        calculateDistance(documentDto.getLatitude(), documentDto.getLongitude()
                                                , pharmacyDto.getLatitude(), pharmacyDto.getLongitude()))
                                .build())
                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(Direction::getDistance))
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());

    }

    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto) {
        if (Objects.isNull(inputDocumentDto)) return Collections.emptyList();
        //약국 데이터 조회
        return kakaoCategorySearchService.requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM)
                .getDocumentDtoList().stream()
                .map(resultDocumentDto ->
                        Direction.builder()
                                .inputAddress(inputDocumentDto.getAddressName())
                                .inputLatitude(inputDocumentDto.getLatitude())
                                .inputLongitude(inputDocumentDto.getLongitude())
                                .targetAddress(resultDocumentDto.getAddressName())
                                .targetPharmacyName(resultDocumentDto.getPlaceName())
                                .targetLatitude(resultDocumentDto.getLatitude())
                                .targetLongitude(resultDocumentDto.getLongitude())
                                .distance(resultDocumentDto.getDistance() * 0.001)
                                .build())
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
