package com.branches.response;

import com.branches.model.VehicleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleGetResponse {
    private Long id;
    private VehicleType vehicleType;
    private String brand;
    private String model;
    private ClientByVehicleResponse client;
}
