package com.branches.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RepairPostRequest {
    private Long clientId;
    private Long vehicleId;
    private List<RepairPieceByRepairPostRequest> pieces;
    private List<RepairEmployeeByRepairPostRequest> employees;
    private LocalDate endDate;
}
