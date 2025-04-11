package com.branches.response;

import com.branches.model.VehicleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleByRepairGetResponse {
    private VehicleType vehicleType;
    private String brand;
    private String model;
}
