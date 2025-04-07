package com.branches.mapper;

import com.branches.model.Vehicle;
import com.branches.request.VehiclePostRequest;
import com.branches.response.VehicleClientGetResponse;
import com.branches.response.VehicleGetResponse;
import com.branches.response.VehiclePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {
    List<VehicleGetResponse> toVehicleGetResponseList(List<Vehicle> vehicleList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    Vehicle toVehicle(VehiclePostRequest postRequest);

    VehiclePostResponse toVehiclePostResponse(Vehicle vehicle);

    List<VehicleClientGetResponse> toVehicleClientGetResponseList(List<Vehicle> VehicleClientGetResponseList);
}
