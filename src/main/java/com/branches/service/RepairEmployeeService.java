package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairEmployeeMapper;
import com.branches.model.Category;
import com.branches.model.Employee;
import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.repository.RepairEmployeeRepository;
import com.branches.request.RepairEmployeePostRequest;
import com.branches.response.RepairEmployeePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepairEmployeeService {
    private final RepairEmployeeRepository repository;
    private final RepairService repairService;
    private final EmployeeService employeeService;
    private final RepairEmployeeMapper mapper;

    public List<RepairEmployeePostResponse> findAllByRepairId(Long repairId) {
        Repair repair = repairService.findByIdOrThrowsNotFoundException(repairId);

        List<RepairEmployee> response = repository.findAllByRepair(repair);

        return mapper.toRepairEmployeePostResponseList(response);
    }

    private RepairEmployee findByRepairAndEmployeeOrThrowsNotFoundException(Repair repair, Employee employee) {
        return findByRepairIdAndEmployeeId(repair.getId(), employee.getId())
                .orElseThrow(() -> new NotFoundException("The employee was not found in the repair"));
    }

    private Optional<RepairEmployee> findByRepairIdAndEmployeeId(Long repairId, Long employeeId) {
        return repository.findByRepair_IdAndEmployee_Id(repairId, employeeId);
    }

    @Transactional
    public RepairEmployeePostResponse save(Long repairId, RepairEmployeePostRequest postRequest) {
        Repair repair = repairService.findByIdOrThrowsNotFoundException(repairId);

        Employee employee = employeeService.findByIdOrThrowsNotFoundException(postRequest.getEmployeeId());
        Category category = employee.getCategory();

        Integer hoursWorked = postRequest.getHoursWorked();
        RepairEmployee repairEmployeeToSave = RepairEmployee.builder().repair(repair).employee(employee).hoursWorked(hoursWorked).totalValue(category.getHourlyPrice() * hoursWorked).build();

        Optional<RepairEmployee> optionalRepairEmployee = findByRepairIdAndEmployeeId(repairId, employee.getId());
        optionalRepairEmployee.ifPresent(foundRepairEmployee -> {
            int totalHoursWorked = foundRepairEmployee.getHoursWorked() + repairEmployeeToSave.getHoursWorked();

            repairEmployeeToSave.setId(foundRepairEmployee.getId());
            repairEmployeeToSave.setHoursWorked(totalHoursWorked);
            repairEmployeeToSave.setTotalValue(totalHoursWorked * category.getHourlyPrice());
        });

        RepairEmployee repairEmployeeSaved = repository.save(repairEmployeeToSave);

        repairService.updateTotalValue(repair.getId(), repairEmployeeSaved.getTotalValue());

        return mapper.toRepairEmployeePostResponse(repairEmployeeSaved);
    }

    @Transactional
    public void deleteByRepairIdAndEmployeeId(Long repairId, Long employeeId) {
        Repair repair = repairService.findByIdOrThrowsNotFoundException(repairId);
        Employee employee = employeeService.findByIdOrThrowsNotFoundException(employeeId);

        RepairEmployee repairEmployeeToDelete = findByRepairAndEmployeeOrThrowsNotFoundException(repair, employee);
        double totalValue = repairEmployeeToDelete.getTotalValue();

        repository.deleteById(repairEmployeeToDelete.getId());

        repairService.updateTotalValue(repair.getId(), -totalValue);
    }
}
