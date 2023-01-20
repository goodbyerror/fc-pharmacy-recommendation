package com.example.pharmacy.api.service

import com.example.pharmacy.AbstractIntegrationContainerBaseTest
import com.example.pharmacy.api.dto.KakaoApiResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class KakaoAddressSearchServiceTest extends AbstractIntegrationContainerBaseTest {
    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService;

    def "KakaoAddressSearchService를 테스트한다."() {
        given:

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(null)

        then:
        assert result == null
    }

    def "정상적인 주소값을 전달했을 때, 결과를 확인하는 테스트"() {
        given:
        def address = "서울시 서대문구 거북골로 100"

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address)

        then:
        result.documentDtoList.size() > 0
        result.metaDto.totalCount > 0
        result.documentDtoList.get(0).addressName != null
    }

    def "정상적인 주소를 입력했을 때, 정상적으로 위도 경도를 반환한다."() {
        given:
        boolean actualResult = false

        when:
        def searchResult = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        if (searchResult == null) actualResult = false
        else actualResult = searchResult.getDocumentDtoList().size() > 0


        where:
        inputAddress                            | expectedResult
        "서울 특별시 성북구 종암동"                   | true
        "서울 성북구 종암동 91"                     | true
        "서울 대학로"                             | true
        "서울 성북구 종암동 잘못된 주소"               | false
        "광진구 구의동 251-45"                     | true
        "광진구 구의동 251-455555"                 | false
        ""                                      | false
    }
}
