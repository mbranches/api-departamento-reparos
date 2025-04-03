package com.branches.repository;

import com.branches.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByStreetAndDistrictAndCityAndState(String street, String district, String city, String state);
}
