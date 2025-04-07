package com.branches.response;

import com.branches.model.VehicleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VehicleClientGetResponse {
    private VehicleType vehicleType;
    private String brand;
    private String model;
}
