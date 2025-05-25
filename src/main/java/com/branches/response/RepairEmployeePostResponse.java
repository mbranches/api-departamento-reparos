package com.branches.response;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@With
@Builder
public class RepairEmployeePostResponse {
    private EmployeeByRepairResponse employee;
    private int hoursWorked;
    private double totalValue;
}
