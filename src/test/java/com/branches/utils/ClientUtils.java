package com.branches.utils;

import com.branches.model.*;
import com.branches.request.ClientPostRequest;
import com.branches.request.ClientPutRequest;
import com.branches.response.*;

import java.util.ArrayList;
import java.util.List;

public class ClientUtils {

    public static List<Client> newClientList() {
        List<Person> personList = PersonUtils.newPersonList();
        Person person1 = personList.getFirst();
        Person person2 = personList.get(1);
        Person person3 = personList.getLast();

        Client client1 = Client.builder().id(1L).person(person1).email("marcus@gmail.com").build();
        Client client2 = Client.builder().id(2L).person(person2).email("vinicius@gmail.com").build();
        Client client3 = Client.builder().id(3L).person(person3).email("mario@gmail.com").build();

        return new ArrayList<>(List.of(client1, client2, client3));
    }

    public static List<ClientGetResponse> newClientGetResponseList() {
        List<Person> personList = PersonUtils.newPersonList();
        Person person1 = personList.getFirst();
        Person person2 = personList.get(1);
        Person person3 = personList.getLast();

        ClientGetResponse client1 = ClientGetResponse.builder().id(1L).person(person1).email("marcus@gmail.com").build();
        ClientGetResponse client2 = ClientGetResponse.builder().id(2L).person(person2).email("vinicus@gmail.com").build();
        ClientGetResponse client3 = ClientGetResponse.builder().id(3L).person(person3).email("mario@gmail.com").build();

        return new ArrayList<>(List.of(client1, client2, client3));
    }

    public static ClientPostRequest newClientPostRequest() {
        Address address = AddressUtils.newAddressToSave();

        ClientPostRequest client = ClientPostRequest.builder().name("Chispirito").lastName("Costa").email("chispirito@gmail.com").address(address).build();
        Phone phone = PhoneUtils.newPhone(4L);
        client.setPhones(List.of(phone));

        return client;
    }

    public static Client newClientToSave() {
        Person person = PersonUtils.newPersonToSave();

        return Client.builder().person(person).email("chispirito@gmail.com").build();
    }

    public static Client newClientSaved() {
        return newClientToSave().withId(4L);
    }

    public static ClientDefaultResponse newClientDefaultResponse() {
        PersonDefaultResponse person = PersonUtils.newPersonDefaultResponse();

        return ClientDefaultResponse.builder()
                .id(4L)
                .person(person)
                .email("chispirito@gmail.com")
                .build();
    }

    public static ClientPostResponse newClientPostResponse() {
        Person person = PersonUtils.newPersonSaved();

        return ClientPostResponse.builder().id(4L).person(person).email("chispirito@gmail.com").build();
    }

    public static ClientDefaultResponse newClientByRepairPostResponse() {
        PersonDefaultResponse person = PersonUtils.newPersonDefaultResponse();

        return ClientDefaultResponse.builder()
                .id(4L)
                .person(person)
                .email("chispirito@gmail.com")
                .build();
    }

    public static ClientPutRequest newClientPutRequest() {
        Person person = PersonUtils.newPersonList().getFirst();

        return ClientPutRequest.builder()
                .id(1L)
                .name("Novo Nome")
                .lastName("Novo Sobrenome")
                .address(person.getAddress())
                .phones(person.getPhones())
                .email("marcus@gmail.com")
                .build();
    }

    public static Client newClientToUpdate() {
        Person personToUpdate = PersonUtils.newPersonToUpdate();

        return newClientList().getFirst().withPerson(personToUpdate);
    }
}
