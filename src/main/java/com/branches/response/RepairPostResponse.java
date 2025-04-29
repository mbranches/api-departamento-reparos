package com.branches.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RepairPostResponse {
    private Long id;
    private ClientDefaultResponse client;
    private VehicleByRepairPostResponse vehicle;
    private List<RepairPieceByRepairResponse> pieces;
    private List<RepairEmployeeByRepairResponse> employees;
    private double totalValue;
    private LocalDate endDate;
}
