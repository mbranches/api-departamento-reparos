package com.branches.service;

import com.branches.mapper.VehicleMapper;
import com.branches.model.Client;
import com.branches.model.Vehicle;
import com.branches.repository.VehicleRepository;
import com.branches.request.VehiclePostRequest;
import com.branches.response.VehicleClientGetResponse;
import com.branches.response.VehicleGetResponse;
import com.branches.response.VehiclePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository repository;
    private final VehicleMapper mapper;
    private final ClientService clientService;

    public List<VehicleGetResponse> findAll() {
        return mapper.toVehicleGetResponseList(
                repository.findAll()
        );
    }

    public VehiclePostResponse save(VehiclePostRequest postRequest) {
        Client clientFound = clientService.findByIdOrElseThrowsNotFoundException(postRequest.getClientId());

        Vehicle vehicleToSave = mapper.toVehicle(postRequest);
        vehicleToSave.setClient(clientFound);

        Vehicle response = repository.save(vehicleToSave);

        return mapper.toVehiclePostResponse(response);
    }

    public List<VehicleClientGetResponse> findByClientId(Long clientId) {
        List<Vehicle> foundVehicles = repository.findAllByClient(clientService.findByIdOrElseThrowsNotFoundException(clientId));

        return mapper.toVehicleClientGetResponseList(foundVehicles);
    }
}
