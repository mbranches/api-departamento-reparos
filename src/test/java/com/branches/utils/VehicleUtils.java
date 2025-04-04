package com.branches.utils;

import com.branches.model.Vehicle;
import com.branches.model.VehicleType;
import com.branches.response.ClientGetResponse;
import com.branches.response.VehicleGetResponse;

import java.util.ArrayList;
import java.util.List;

public class VehicleUtils {
    public static List<Vehicle> newVehicleList() {
        Vehicle vehicle1 = Vehicle.builder().id(1L).vehicleType(VehicleType.carro).brand("Fiat").model("Toro").client(ClientUtils.newClientToSave()).build();
        Vehicle vehicle2 = Vehicle.builder().id(2L).vehicleType(VehicleType.moto).brand("Yamaha").model("Mt-07").client(ClientUtils.newClientToSave()).build();
        Vehicle vehicle3 = Vehicle.builder().id(3L).vehicleType(VehicleType.moto).brand("Honda").model("Biz").client(ClientUtils.newClientToSave()).build();

        return new ArrayList<>(List.of(vehicle1, vehicle2, vehicle3));
    }

    public static List<VehicleGetResponse> newVehicleGetResponseList() {
        ClientGetResponse clientGetResponse = ClientUtils.newClientGetResponse();

        VehicleGetResponse vehicle1 = VehicleGetResponse.builder().vehicleType(VehicleType.carro).brand("Fiat").model("Toro").client(clientGetResponse).build();
        VehicleGetResponse vehicle2 = VehicleGetResponse.builder().vehicleType(VehicleType.moto).brand("Yamaha").model("Mt-07").client(clientGetResponse).build();
        VehicleGetResponse vehicle3 = VehicleGetResponse.builder().vehicleType(VehicleType.moto).brand("Honda").model("Biz").client(clientGetResponse).build();

        return new ArrayList<>(List.of(vehicle1, vehicle2, vehicle3));
    }
}
