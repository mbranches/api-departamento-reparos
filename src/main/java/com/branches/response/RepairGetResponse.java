package com.branches.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RepairGetResponse {
    private Long id;
    private ClientByRepairGetResponse client;
    private VehicleByRepairGetResponse vehicle;
    private double totalValue;
    private LocalDate endDate;
}
