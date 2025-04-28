package com.branches.repository;

import com.branches.model.Phone;
import com.branches.model.PhoneType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    Optional<Phone> findByNumberAndPhoneType(String number, PhoneType phoneType);
}
