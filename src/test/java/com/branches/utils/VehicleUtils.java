package com.branches.utils;

import com.branches.model.Client;
import com.branches.model.Vehicle;
import com.branches.model.VehicleType;
import com.branches.request.VehiclePostRequest;
import com.branches.response.*;

import java.util.ArrayList;
import java.util.List;

public class VehicleUtils {
    public static List<Vehicle> newVehicleList() {
        Client client = ClientUtils.newClientSaved();

        Vehicle vehicle1 = Vehicle.builder().id(1L).vehicleType(VehicleType.carro).brand("Fiat").model("Toro").client(client).build();
        Vehicle vehicle2 = Vehicle.builder().id(2L).vehicleType(VehicleType.moto).brand("Yamaha").model("Mt-07").client(client).build();
        Vehicle vehicle3 = Vehicle.builder().id(3L).vehicleType(VehicleType.moto).brand("Honda").model("Biz").client(client).build();

        return new ArrayList<>(List.of(vehicle1, vehicle2, vehicle3));
    }

    public static List<VehicleGetResponse> newVehicleGetResponseList() {
        ClientDefaultResponse client = ClientUtils.newClientDefaultResponse();

        VehicleGetResponse vehicle1 = VehicleGetResponse.builder().id(1L).vehicleType(VehicleType.carro).brand("Fiat").model("Toro").client(client).build();
        VehicleGetResponse vehicle2 = VehicleGetResponse.builder().id(2L).vehicleType(VehicleType.moto).brand("Yamaha").model("Mt-07").client(client).build();
        VehicleGetResponse vehicle3 = VehicleGetResponse.builder().id(3L).vehicleType(VehicleType.moto).brand("Honda").model("Biz").client(client).build();

        return new ArrayList<>(List.of(vehicle1, vehicle2, vehicle3));
    }

    public static Vehicle newVehicleToSave() {
        return Vehicle.builder()
                .id(4L)
                .vehicleType(VehicleType.caminhao)
                .brand("Scania")
                .model("T113")
                .client(ClientUtils.newClientSaved()).build();
    }

    public static Vehicle newVehicleSaved() {
        return newVehicleToSave().withId(4L);
    }

    public static VehiclePostRequest newVehiclePostRequest() {
        Client client = ClientUtils.newClientList().getFirst();
        return VehiclePostRequest.builder()
                .vehicleType(VehicleType.caminhao)
                .brand("Scania")
                .model("T113")
                .clientId(client.getId())
                .build();
    }

    public static VehiclePostResponse newVehiclePostResponse() {
        ClientDefaultResponse client = ClientUtils.newClientDefaultResponse();
        return VehiclePostResponse.builder()
                .id(4L)
                .vehicleType(VehicleType.caminhao)
                .brand("Scania")
                .model("T113")
                .client(client).build();
    }

    public static List<VehicleDefaultResponse> newVehicleClientGetReponseList() {
        VehicleDefaultResponse vehicle1 = VehicleDefaultResponse.builder().vehicleType(VehicleType.carro).brand("Fiat").model("Toro").build();
        VehicleDefaultResponse vehicle2 = VehicleDefaultResponse.builder().vehicleType(VehicleType.moto).brand("Yamaha").model("Mt-07").build();
        VehicleDefaultResponse vehicle3 = VehicleDefaultResponse.builder().vehicleType(VehicleType.moto).brand("Honda").model("Biz").build();

        return List.of(vehicle1, vehicle2, vehicle3);
    }

    public static VehicleDefaultResponse newVehicleDefaultResponse() {
        return VehicleDefaultResponse.builder()
                .id(1L)
                .vehicleType(VehicleType.carro)
                .brand("Fiat")
                .model("Toro")
                .build();
    }
}
