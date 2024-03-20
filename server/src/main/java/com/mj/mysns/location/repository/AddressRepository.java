package com.mj.mysns.location.repository;

import com.mj.mysns.location.entity.LegalAddress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<LegalAddress, Long>, CustomizedAddressRepository {

    Optional<LegalAddress> findByCode(String code);
}
