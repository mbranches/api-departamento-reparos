package com.branches.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairEmployeeByRepairResponse {
    private EmployeeByRepairResponse employee;
    private int hoursWorked;
    private double totalValue;
}
