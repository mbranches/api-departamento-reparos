package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.model.Employee;
import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.repository.RepairEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairEmployeeService {
    private final RepairEmployeeRepository repository;

    public List<RepairEmployee> findAllByRepair(Repair repair) {
        return repository.findAllByRepair(repair);
    }

    public RepairEmployee findByRepairAndEmployeeOrThrowsNotFoundException(Repair repair, Employee employee) {
        return repository.findByRepairAndEmployee(repair, employee)
                .orElseThrow(() -> new NotFoundException("The employee was not found in the repair"));
    }

    public List<RepairEmployee> saveAll(List<RepairEmployee> repairPiecesToSave) {
        return repository.saveAll(repairPiecesToSave);
    }

    public void deleteByRepairAndEmployee(Repair repair, Employee employee) {
        repository.delete(findByRepairAndEmployeeOrThrowsNotFoundException(repair, employee));
    }
}
