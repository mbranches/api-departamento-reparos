package com.branches.service;

import com.branches.model.Address;
import com.branches.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository repository;

    public Optional<Address> findAddress(Address address) {
        return repository.findByStreetAndDistrictAndCityAndState(
                address.getStreet(),
                address.getDistrict(),
                address.getCity(),
                address.getState()
        );
    }

    public Address save(Address address) {
        return repository.save(address);
    }
}
