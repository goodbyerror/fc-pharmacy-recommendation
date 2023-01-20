package com.example.pharmacy.service

import com.example.pharmacy.AbstractIntegrationContainerBaseTest
import com.example.pharmacy.pharmacy.entity.Pharmacy
import com.example.pharmacy.pharmacy.repository.PharmacyRepository
import com.example.pharmacy.pharmacy.service.PharmacyRepositoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.time.LocalDateTime

@SpringBootTest
class PharmacyRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {
    @Autowired
    private PharmacyRepositoryService pharmacyRepositoryService

    @Autowired
    private PharmacyRepository pharmacyRepository

    def setup() {
        pharmacyRepository.deleteAll()
    }

    def "transaction test"() {
        given:
        String originAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 특별시 서대문구 거북골로"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(originAddress)
                .pharmacyName(name)
                .build()

        when:
        def entity = pharmacyRepository.save(pharmacy)
        def result = pharmacyRepositoryService.updateAddress(entity.getId(), modifiedAddress)

        def entity2 = pharmacyRepository.findById(entity.getId()).orElse(null)

        then:
        entity2.getPharmacyAddress() == modifiedAddress

    }

    def "transaction test fail"() {
        given:
        String originAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 특별시 서대문구 거북골로"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(originAddress)
                .pharmacyName(name)
                .build()

        when:
        def entity = pharmacyRepository.save(pharmacy)
        def result = pharmacyRepositoryService.updateAddressWithoutTransaction(entity.getId(), modifiedAddress)

        def entity2 = pharmacyRepository.findById(entity.getId()).orElse(null)

        then:
        entity2.getPharmacyAddress() == originAddress

    }

    def "BaseTimeEntity 등록 테스트"() {
        given:
        LocalDateTime localDateTime = LocalDateTime.now()
        String address = "서울시 서대문구 거불골로"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyName(name)
                .pharmacyAddress(address)
                .build()

        when:
        def entity = pharmacyRepository.save(pharmacy)
        def pharmacyList = pharmacyRepository.findAll()

        def result = pharmacyList.get(0)

        then:
        result.getCreatedDate().isAfter(localDateTime)
        result.getModifiedDate().isAfter(localDateTime)
    }

    def "self invocation"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        pharmacyRepositoryService.bar(Arrays.asList(pharmacy))

        then:
        def e = thrown(RuntimeException.class)
        def list = pharmacyRepositoryService.findAll()
        list.size() == 1
    }
}
