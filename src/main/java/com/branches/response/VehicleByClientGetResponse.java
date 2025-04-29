package com.branches.response;

import com.branches.model.VehicleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VehicleByClientGetResponse {
    private Long id;
    private VehicleType vehicleType;
    private String brand;
    private String model;
}
