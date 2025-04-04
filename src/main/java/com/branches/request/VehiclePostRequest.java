package com.branches.request;

import com.branches.model.Client;
import com.branches.model.VehicleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehiclePostRequest {
    private VehicleType vehicleType;
    private String brand;
    private String model;
    private Long clientId;
}