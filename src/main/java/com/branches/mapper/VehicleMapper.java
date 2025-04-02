package com.branches.mapper;

import com.branches.model.Vehicle;
import com.branches.response.VehicleGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {
    List<VehicleGetResponse> toVehicleGetResponseList(List<Vehicle> vehicleList);
}
