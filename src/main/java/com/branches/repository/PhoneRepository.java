package com.branches.repository;

import com.branches.model.Person;
import com.branches.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    Optional<Phone> findByNumber(String number);

    Optional<Object> findByNumberAndPerson_IdNot(String number, Long personId);

    Optional<Phone> findByNumberAndPerson_Id(String number, Long personId);
}
