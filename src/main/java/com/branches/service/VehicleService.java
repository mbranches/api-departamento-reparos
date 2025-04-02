package com.branches.service;

import com.branches.model.Vehicle;
import com.branches.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository repository;

    public List<Vehicle> findAll() {
        return repository.findAll();
    }
}
