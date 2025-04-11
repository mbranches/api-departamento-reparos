package com.branches.utils;

import com.branches.model.Address;
import com.branches.model.Client;
import com.branches.model.Phone;
import com.branches.model.PhoneType;
import com.branches.request.ClientPostRequest;
import com.branches.response.*;

import java.util.ArrayList;
import java.util.List;

public class ClientUtils {

    public static List<Client> newClientList() {
        Address address = AddressUtils.newAddressToSave();

        Client client1 = Client.builder().id(1L).name("Marcus").lastName("Branches").address(address).build();
        Phone phone1 = Phone.builder().id(1L).client(client1).number("5959559").phoneType(PhoneType.celular).build();
        client1.setPhones(List.of(phone1));

        Client client2 = Client.builder().id(2L).name("Vinicius").lastName("Lima").address(address).build();
        Phone phone2 = Phone.builder().id(2L).client(client2).number("2222222").phoneType(PhoneType.celular).build();
        client2.setPhones(List.of(phone2));

        Client client3 = Client.builder().id(3L).name("Vinicius").lastName("Lima").address(address).build();
        Phone phone3 = Phone.builder().id(3L).client(client3).number("3333333").phoneType(PhoneType.celular).build();
        client3.setPhones(List.of(phone3));

        return new ArrayList<>(List.of(client1, client2, client3));
    }

    public static List<ClientGetResponse> newClientGetResponseList() {
        AddressGetResponse addressGetResponse = AddressGetResponse.builder().street("Almirante Barroso").district("São Brás").city("Belém").state("Pa").build();;

        ClientGetResponse client1 = ClientGetResponse.builder().name("Marcus").lastName("Branches").address(addressGetResponse).build();
        PhoneGetResponse phone1 = PhoneGetResponse.builder().number("5959559").phoneType(PhoneType.celular).build();
        client1.setPhones(List.of(phone1));

        ClientGetResponse client2 = ClientGetResponse.builder().name("Vinicius").lastName("Lima").address(addressGetResponse).build();
        PhoneGetResponse phone2 = PhoneGetResponse.builder().number("2222222").phoneType(PhoneType.celular).build();
        client2.setPhones(List.of(phone2));

        ClientGetResponse client3 = ClientGetResponse.builder().name("Vinicius").lastName("Lima").address(addressGetResponse).build();
        PhoneGetResponse phone3 = PhoneGetResponse.builder().number("3333333").phoneType(PhoneType.celular).build();
        client3.setPhones(List.of(phone3));

        return new ArrayList<>(List.of(client1, client2, client3));
    }

    public static ClientPostRequest newClientPostRequest() {
        Address address = AddressUtils.newAddressToSave();

        ClientPostRequest client = ClientPostRequest.builder().name("Chispirito").lastName("Costa").address(address).build();
        Phone phone = PhoneUtils.newPhone(4L);
        client.setPhones(List.of(phone));

        return client;
    }

    public static Client newClientToSave() {
        Address address = AddressUtils.newAddressToSave();

        Client client = Client.builder().id(4L).name("Chispirito").lastName("Costa").address(address).build();
        Phone phone = PhoneUtils.newPhone(4L);
        client.setPhones(List.of(phone));

        return client;
    }

    public static ClientByVehicleGetResponse newClientVehicleGetResponse() {
        return ClientByVehicleGetResponse.builder()
                .name("Chispirito")
                .lastName("Costa")
                .build();
    }

    public static ClientByRepairGetResponse newClientByRepairGetResponse() {
        return ClientByRepairGetResponse.builder()
                .name("Chispirito")
                .lastName("Costa")
                .build();
    }

    public static ClientPostResponse newClientPostResponse() {
        Address address = AddressUtils.newAddressToSave();

        ClientPostResponse client = ClientPostResponse.builder().id(4L).name("Chispirito").lastName("Costa").address(address).build();
        Phone phone = PhoneUtils.newPhone(4L);
        client.setPhones(List.of(phone));

        return client;
    }

    public static ClientByRepairPostResponse newClientByRepairPostResponse() {
        return ClientByRepairPostResponse.builder()
                .name("Chispirito")
                .lastName("Costa")
                .build();
    }
}
