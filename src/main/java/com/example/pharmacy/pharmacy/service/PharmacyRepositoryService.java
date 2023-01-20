package com.example.pharmacy.pharmacy.service;

import com.example.pharmacy.pharmacy.entity.Pharmacy;
import com.example.pharmacy.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRepositoryService {
    private final  PharmacyRepository pharmacyRepository;

    public void bar(List<Pharmacy> pharmacies) {
        log.info("bar call CurrentTransactionName" + TransactionSynchronizationManager.getCurrentTransactionName());

        foo(pharmacies);
    }

    @Transactional
    public void foo(List<Pharmacy> pharmacies) {
        log.info("bar call CurrentTransactionName" + TransactionSynchronizationManager.getCurrentTransactionName());
        pharmacies.forEach((pharmacy) -> {
            pharmacyRepository.save(pharmacy);
            throw new RuntimeException("error");
        });
    }

    @Transactional
    public void updateAddress(Long id, String address) {
        Pharmacy pharmacy = pharmacyRepository.findById(id).orElse(null);

        if (Objects.isNull(pharmacy)) {
            log.error("[PharmacyRepositoryService updateAddress] id not found {}", id);
            return;
        }

        pharmacy.changePharmacyAddress(address);
    }

    public void updateAddressWithoutTransaction(Long id, String address) {
        Pharmacy pharmacy = pharmacyRepository.findById(id).orElse(null);

        if (Objects.isNull(pharmacy)) {
            log.error("[PharmacyRepositoryService updateAddress] id not found {}", id);
            return;
        }

        pharmacy.changePharmacyAddress(address);
    }

    @Transactional(readOnly = true)
    public List<Pharmacy> findAll() {
        return pharmacyRepository.findAll();
    }
}
