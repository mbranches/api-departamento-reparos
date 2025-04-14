package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairEmployeeMapper;
import com.branches.mapper.RepairMapper;
import com.branches.mapper.RepairPieceMapper;
import com.branches.model.*;
import com.branches.repository.RepairRepository;
import com.branches.request.RepairEmployeeByRepairPostRequest;
import com.branches.request.RepairPieceByRepairPostRequest;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairEmployeeByRepairResponse;
import com.branches.response.RepairGetResponse;
import com.branches.response.RepairPieceByRepairResponse;
import com.branches.response.RepairPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final EmployeeService employeeService;
    private final PieceService pieceService;

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
                .orElseThrow(() -> new NotFoundException("Repair not Found"));
    }

    public List<RepairEmployeeByRepairResponse> findEmployeesByRepairId(Long repairId) {
        Repair repair = findByIdOrThrowsNotFoundException(repairId);

        List<RepairEmployee> response = findEmployeesByRepair(repair);

        return repairEmployeeMapper.toRepairEmployeeByRepairResponseList(response);
    }

    public List<RepairEmployee> findEmployeesByRepair(Repair repair) {
        return repairEmployeeService.findAllByRepair(repair);
    }

    public List<RepairPieceByRepairResponse> findPiecesByRepairId(Long repairId) {
        Repair repair = findByIdOrThrowsNotFoundException(repairId);

        List<RepairPiece> response = findPiecesByRepair(repair);

        return repairPieceMapper.toRepairPieceByRepairResponseList(response);
    }

    private List<RepairPiece> findPiecesByRepair(Repair repair) {
        return repairPieceService.findAllByRepair(repair);
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

        List<RepairPieceByRepairPostRequest> piecesRequest = Optional.ofNullable(postRequest.getPieces()).orElse(Collections.emptyList());
        List<RepairPiece> repairPiecesToSave = repairPieceMapper.toRepairPieceList(piecesRequest);

        List<RepairEmployeeByRepairPostRequest> employeesRequest = Optional.ofNullable(postRequest.getEmployees()).orElse(Collections.emptyList());
        List<RepairEmployee> repairEmployeesToSave = repairEmployeeMapper.toRepairEmployeeList(employeesRequest);

        Repair repairToSave = Repair.builder()
                .client(client)
                .vehicle(vehicle)
                .totalValue(calculatesTotalValue(repairEmployeesToSave, repairPiecesToSave))
                .endDate(postRequest.getEndDate())
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

    public void deleteById(Long id) {
        Repair repairToDelete = findByIdOrThrowsNotFoundException(id);

        repository.delete(repairToDelete);
    }

    public void removesRepairEmployeeById(Long repairId, Long employeeId) {
        Repair repair = findByIdOrThrowsNotFoundException(repairId);
        Employee employee = employeeService.findByIdOrThrowsNotFoundException(employeeId);

        repairEmployeeService.deleteByRepairAndEmployee(repair, employee);

        updateTotalValue(repair);
    }

    public void removesRepairPieceById(Long repairId, Long pieceId) {
        Repair repair = findByIdOrThrowsNotFoundException(repairId);
        Piece piece = pieceService.findByIdOrThrowsNotFoundException(pieceId);

        repairPieceService.deleteByRepairAndPiece(repair, piece);

        updateTotalValue(repair);
    }

    public void updateTotalValue(Repair repair) {
        List<RepairEmployee> repairEmployees = findEmployeesByRepair(repair);
        List<RepairPiece> repairPieces = findPiecesByRepair(repair);

        repair.setTotalValue(calculatesTotalValue(repairEmployees, repairPieces));

        repository.save(repair);
    }

    @Transactional
    public List<RepairEmployeeByRepairResponse> addEmployee(Long repairId, List<RepairEmployeeByRepairPostRequest> postRequestList) {
        Repair repair = findByIdOrThrowsNotFoundException(repairId);

        List<RepairEmployee> repairEmployeeToSaveList = repairEmployeeMapper.toRepairEmployeeList(postRequestList);

        List<RepairEmployee> employees = repairEmployeeService.findAllByRepair(repair);

        for (RepairEmployee repairEmployeeToSave : repairEmployeeToSaveList) {
            repairEmployeeToSave.setRepair(repair);
            repairEmployeeToSave.setId(
                    new RepairEmployeeKey(repairId, repairEmployeeToSave.getEmployee().getId())
            );
            Category category = repairEmployeeToSave.getEmployee().getCategory();

            if (employees.contains(repairEmployeeToSave)) {
                RepairEmployee repairEmployeeToUpdate = employees.get(employees.indexOf(repairEmployeeToSave));

                repairEmployeeToUpdate.setHoursWorked(repairEmployeeToUpdate.getHoursWorked() + repairEmployeeToSave.getHoursWorked());
                repairEmployeeToUpdate.setTotalValue(category.getHourlyPrice() * repairEmployeeToUpdate.getHoursWorked());
            } else {
                repairEmployeeToSave.setTotalValue(category.getHourlyPrice() * repairEmployeeToSave.getHoursWorked());
                employees.add(repairEmployeeToSave);
            }
        }

        List<RepairEmployee> repairEmployeesSaved = repairEmployeeService.saveAll(employees);

        updateTotalValue(repair);

        return repairEmployeeMapper.toRepairEmployeeByRepairResponseList(repairEmployeesSaved);
    }
}
