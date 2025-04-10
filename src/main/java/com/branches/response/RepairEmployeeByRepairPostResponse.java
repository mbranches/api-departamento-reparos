package com.branches.response;

import com.branches.model.Employee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairEmployeeByRepairPostResponse {
    private EmployeeByRepairPostResponse employee;
    private int hoursWorked;
    private double totalValue;
}
