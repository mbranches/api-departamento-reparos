package com.branches.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RepairPostResponse {
    private Long id;
    private ClientDefaultResponse client;
    private VehicleDefaultResponse vehicle;
    private List<RepairPieceByRepairResponse> pieces;
    private List<RepairEmployeeByRepairResponse> employees;
    private double totalValue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate endDate;
}
