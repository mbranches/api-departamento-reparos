package com.branches.utils;

import com.branches.model.*;
import com.branches.response.PersonDefaultResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonUtils {
    public static List<Person> newPersonList() {
        Address address = AddressUtils.newAddressSaved();

        Person person1 = Person.builder().id(1L).name("Marcus").lastName("Branches").address(address).build();
        Phone phone1 = Phone.builder().id(1L).person(person1).number("5959559").phoneType(PhoneType.celular).build();
        person1.setPhones(Collections.singletonList(phone1));

        Person person2 = Person.builder().id(2L).name("Vinicius").lastName("Lima").address(address).build();
        Phone phone2 = Phone.builder().id(2L).person(person2).number("2222222").phoneType(PhoneType.celular).build();
        person2.setPhones(Collections.singletonList(phone2));

        Person person3 = Person.builder().id(3L).name("Mario").lastName("Andrade").address(address).build();
        Phone phone3 = Phone.builder().id(3L).person(person3).number("3333333").phoneType(PhoneType.celular).build();
        person3.setPhones(Collections.singletonList(phone3));

        return new ArrayList<>(List.of(person1, person2, person3));
    }

    public static Person newPersonToSave() {
        Address address = AddressUtils.newAddressToSave();

        Person person = Person.builder().id(4L).name("Chispirito").lastName("Costa").address(address).build();
        Phone phone = Phone.builder().id(4L).person(person).number("21121521").phoneType(PhoneType.celular).build();
        person.setPhones(Collections.singletonList(phone));

        return person;
    }

    public static Person newPersonSaved() {
        Address address = AddressUtils.newAddressSaved();

        Person person = Person.builder().id(4L).name("Chispirito").lastName("Costa").address(address).build();
        Phone phone = Phone.builder().id(4L).person(person).number("21121521").phoneType(PhoneType.celular).build();
        person.setPhones(Collections.singletonList(phone));

        return person;
    }

    public static PersonDefaultResponse newPersonDefaultResponse() {
        return PersonDefaultResponse.builder()
                .id(4L)
                .name("Chispirito")
                .lastName("Costa")
                .build();
    }

    public static Person newPersonToUpdate() {
        return newPersonList().getFirst().withName("Novo Nome").withLastName("Novo Sobrenome");
    }
}
