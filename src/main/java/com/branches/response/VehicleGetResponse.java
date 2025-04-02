package com.branches.response;

import com.branches.model.VehicleType;
import lombok.Data;

@Data
public class VehicleGetResponse {
    private VehicleType vehicleType;
    private String brand;
    private String model;
    private ClientGetResponse client;
}
