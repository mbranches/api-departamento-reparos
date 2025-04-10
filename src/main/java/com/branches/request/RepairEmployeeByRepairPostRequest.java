package com.branches.request;

import com.branches.model.Employee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairEmployeeByRepairPostRequest {
    private Long employeeId ;
    private int hoursWorked;
}
