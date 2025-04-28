package com.branches.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairEmployeeByRepairPostRequest {
    @NotNull(message = "The field 'employeeId' cannot be null")
    private Long employeeId ;
    @NotNull(message = "The field 'hoursWorked' cannot be null")
    @PositiveOrZero(message = "'hoursWorked' must be equal to or greater than 0")
    private Integer hoursWorked;
}
