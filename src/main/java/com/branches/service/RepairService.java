package com.branches.service;

import com.branches.mapper.RepairEmployeeMapper;
import com.branches.mapper.RepairMapper;
import com.branches.mapper.RepairPieceMapper;
import com.branches.model.*;
import com.branches.repository.RepairRepository;
import com.branches.request.RepairEmployeeByRepairPostRequest;
import com.branches.request.RepairPieceByRepairPostRequest;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairPostResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepairService {
    private final RepairRepository repository;
    private final RepairMapper mapper;
    private final RepairPieceMapper repairPieceMapper;
    private final RepairEmployeeMapper repairEmployeeMapper;
    private final ClientService clientService;
    private final VehicleService vehicleService;
    private final RepairPieceService repairPieceService;
    private final RepairEmployeeService repairEmployeeService;

    public RepairPostResponse save(RepairPostRequest postRequest) {
        Client client = clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId());
        Vehicle vehicle = vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId());

        List<RepairPieceByRepairPostRequest> piecesRequest = Optional.ofNullable(postRequest.getPieces()).orElse(Collections.emptyList());
        List<RepairPiece> repairPiecesToSave = repairPieceMapper.toRepairPieceList(piecesRequest);

        List<RepairEmployeeByRepairPostRequest> employeesRequest = Optional.ofNullable(postRequest.getEmployees()).orElse(Collections.emptyList());
        List<RepairEmployee> repairEmployeesToSave = repairEmployeeMapper.toRepairEmployeeList(employeesRequest);

        Repair repairToSave = Repair.builder()
                .client(client)
                .vehicle(vehicle)
                .totalValue(calculatesTotalValue(repairEmployeesToSave, repairPiecesToSave))
                .build();
        
        Repair savedRepair = repository.save(repairToSave);

        for (RepairPiece repairPiece : repairPiecesToSave) {
            repairPiece.setRepair(savedRepair);
            repairPiece.setId(new RepairPieceKey(savedRepair.getId(), repairPiece.getPiece().getId()));
        }
        for (RepairEmployee repairEmployee : repairEmployeesToSave) {
            repairEmployee.setRepair(savedRepair);
            repairEmployee.setId(new RepairEmployeeKey(savedRepair.getId(), repairEmployee.getEmployee().getId()));
        }

        List<RepairPiece> repairPieces = repairPieceService.saveAll(repairPiecesToSave);
        List<RepairEmployee> repairEmployees = repairEmployeeService.saveAll(repairEmployeesToSave);

        return mapper.toRepairPostResponse(savedRepair, repairPieces, repairEmployees);
    }

    private double calculatesTotalValue(List<RepairEmployee> employees, List<RepairPiece> pieces) {
        double totalValueEmployees = employees.stream().mapToDouble(RepairEmployee::getTotalValue).sum();
        double totalValuePieces = pieces.stream().mapToDouble(RepairPiece::getTotalValue).sum();

        return totalValueEmployees + totalValuePieces;
    }
}
