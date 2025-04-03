package com.branches.request;

import com.branches.model.Client;
import com.branches.model.VehicleType;
import lombok.Data;

@Data
public class VehiclePostRequest {
    private VehicleType vehicleType;
    private String brand;
    private String model;
    private Long clientId;
}