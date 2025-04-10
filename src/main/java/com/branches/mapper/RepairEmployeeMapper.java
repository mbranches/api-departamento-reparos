package com.branches.mapper;

import com.branches.exception.BadRequestException;
import com.branches.model.Employee;
import com.branches.model.RepairEmployee;
import com.branches.request.RepairEmployeeByRepairPostRequest;
import com.branches.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RepairEmployeeMapper {
    private final EmployeeService employeeService;

    public List<RepairEmployee> toRepairEmployeeList(List<RepairEmployeeByRepairPostRequest> postRequest) {
        try {
            return postRequest.stream().map(this::toRepairEmployee).toList();
        } catch (Exception e) {
            throw new BadRequestException("Error saving employees");
        }
    }

    public RepairEmployee toRepairEmployee(RepairEmployeeByRepairPostRequest postRequest) {
        Employee employee = employeeService.findByIdOrNotFoundException(postRequest.getEmployeeId());
        int hoursWorked = postRequest.getHoursWorked();

        RepairEmployee repairEmployee = new RepairEmployee();
        repairEmployee.setEmployee(employee);
        repairEmployee.setHoursWorked(hoursWorked);
        repairEmployee.setTotalValue(employee.getCategory().getHourlyPrice() * hoursWorked);

        return repairEmployee;
    }
}
