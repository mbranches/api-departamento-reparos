package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairMapper;
import com.branches.model.*;
import com.branches.repository.RepairRepository;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairGetResponse;
import com.branches.response.RepairPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairService {
    private final RepairRepository repository;
    private final RepairMapper mapper;
    private final ClientService clientService;
    private final VehicleService vehicleService;

    public List<RepairGetResponse> findAll(LocalDate dateRepair) {
        List<Repair> response = dateRepair == null ? repository.findAll() : repository.findByEndDateGreaterThanEqual(dateRepair);

        return mapper.toRepairGetResponseList(response);
    }

    public RepairGetResponse findById(Long id) {
        Repair foundRepair = findByIdOrThrowsNotFoundException(id);

        return mapper.toRepairGetResponse(foundRepair);
    }

    public Repair findByIdOrThrowsNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Repair with id '%s' not Found".formatted(id)));
    }

    public List<RepairGetResponse> findAllByClientId(Long clientId) {
        Client client = clientService.findByIdOrThrowsNotFoundException(clientId);

        List<Repair> response = repository.findAllByClient(client);

        return mapper.toRepairGetResponseList(response);
    }

    @Transactional
    public RepairPostResponse save(RepairPostRequest postRequest) {
        Client client = clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId());
        Vehicle vehicle = vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId());

        Repair repairToSave = Repair.builder()
                .client(client)
                .vehicle(vehicle)
                .totalValue(0D)
                .endDate(postRequest.getEndDate())
                .build();

        Repair savedRepair = repository.save(repairToSave);

        return mapper.toRepairPostResponse(savedRepair);
    }

    public void deleteById(Long id) {
        Repair repairToDelete = findByIdOrThrowsNotFoundException(id);

        repository.delete(repairToDelete);
    }

    public void updateTotalValue(Long repairId, Double valueToSum) {
        Repair repair = findByIdOrThrowsNotFoundException(repairId);

        repair.setTotalValue(repair.getTotalValue() + valueToSum);

        repository.save(repair);
    }
}
