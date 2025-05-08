package com.branches.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RepairGetResponse {
    private Long id;
    private ClientDefaultResponse client;
    private VehicleDefaultResponse vehicle;
    private double totalValue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate endDate;
}
