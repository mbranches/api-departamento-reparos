package com.branches.mapper;

import com.branches.exception.BadRequestException;
import com.branches.model.Employee;
import com.branches.model.RepairEmployee;
import com.branches.request.RepairEmployeeByRepairPostRequest;
import com.branches.response.RepairEmployeeByRepairResponse;
import com.branches.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RepairEmployeeMapper {
    private final EmployeeMapper employeeMapper;
    private final EmployeeService employeeService;

    public List<RepairEmployee> toRepairEmployeeList(List<RepairEmployeeByRepairPostRequest> postRequest) {
        return postRequest.stream().map(this::toRepairEmployee).toList();
    }

    public RepairEmployee toRepairEmployee(RepairEmployeeByRepairPostRequest postRequest) {
        Employee employee = employeeService.findByIdOrThrowsNotFoundException(postRequest.getEmployeeId());
        int hoursWorked = postRequest.getHoursWorked();

        RepairEmployee repairEmployee = new RepairEmployee();
        repairEmployee.setEmployee(employee);
        repairEmployee.setHoursWorked(hoursWorked);
        repairEmployee.setTotalValue(employee.getCategory().getHourlyPrice() * hoursWorked);

        return repairEmployee;
    }

    public RepairEmployeeByRepairResponse toRepairEmployeeByRepairResponse(RepairEmployee repairEmployee) {
        return RepairEmployeeByRepairResponse.builder()
                .employee(employeeMapper.toEmployeeByRepairResponse(repairEmployee.getEmployee()))
                .hoursWorked(repairEmployee.getHoursWorked())
                .totalValue(repairEmployee.getTotalValue())
                .build();
    }

    public List<RepairEmployeeByRepairResponse> toRepairEmployeeByRepairResponseList(List<RepairEmployee> repairEmployeeList) {
        return repairEmployeeList.stream().map(this::toRepairEmployeeByRepairResponse).toList();
    }
}
