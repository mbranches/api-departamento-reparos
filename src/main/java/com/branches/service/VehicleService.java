package com.branches.service;

import com.branches.mapper.VehicleMapper;
import com.branches.model.Vehicle;
import com.branches.repository.VehicleRepository;
import com.branches.response.VehicleGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository repository;
    private final VehicleMapper mapper;

    public List<VehicleGetResponse> findAll() {
        return mapper.toVehicleGetResponseList(
                repository.findAll()
        );
    }
}
