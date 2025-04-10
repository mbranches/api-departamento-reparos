package com.branches.response;

import com.branches.model.Client;
import com.branches.model.RepairEmployee;
import com.branches.model.RepairPiece;
import com.branches.model.Vehicle;
import com.branches.request.RepairEmployeeByRepairPostRequest;
import com.branches.request.RepairPieceByRepairPostRequest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RepairPostResponse {
    private Long id;
    private ClientByRepairPostResponse client;
    private VehicleByRepairPostResponse vehicle;
    private List<RepairPieceByRepairPostResponse> pieces;
    private List<RepairEmployeeByRepairPostResponse> employees;
    private double totalValue;
    private LocalDate endDate;
}
