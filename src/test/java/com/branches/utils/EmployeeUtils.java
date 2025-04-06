package com.branches.utils;

import com.branches.model.*;
import com.branches.request.EmployeePostRequest;
import com.branches.response.*;

import java.util.ArrayList;
import java.util.List;

public class EmployeeUtils {

    public static List<Employee> newEmployeeList() {
        Address address = AddressUtils.newAddressToSave();

        Category category = CategoryUtils.newCategoryToSave();

        Employee employee1 = Employee.builder().id(1L).name("Marcus").lastName("Branches").category(category).address(address).build();
        Phone phone1 = Phone.builder().id(1L).employee(employee1).number("5959559").phoneType(PhoneType.celular).build();
        employee1.setPhones(List.of(phone1));

        Employee employee2 = Employee.builder().id(2L).name("Vinicius").lastName("Lima").category(category).address(address).build();
        Phone phone2 = Phone.builder().id(2L).employee(employee2).number("2222222").phoneType(PhoneType.celular).build();
        employee2.setPhones(List.of(phone2));

        Employee employee3 = Employee.builder().id(3L).name("Vinicius").lastName("Lima").category(category).address(address).build();
        Phone phone3 = Phone.builder().id(3L).employee(employee3).number("3333333").phoneType(PhoneType.celular).build();
        employee3.setPhones(List.of(phone3));

        return new ArrayList<>(List.of(employee1, employee2, employee3));
    }

    public static List<EmployeeGetResponse> newEmployeeGetResponseList() {
        AddressGetResponse addressGetResponse = AddressGetResponse.builder().street("Almirante Barroso").district("São Brás").city("Belém").state("Pa").build();;
        CategoryGetResponse categoryGetResponse = CategoryUtils.newCategoryGetResponse();

        EmployeeGetResponse employee1 = EmployeeGetResponse.builder().name("Marcus").lastName("Branches").category(categoryGetResponse).address(addressGetResponse).build();
        PhoneGetResponse phone1 = PhoneGetResponse.builder().number("5959559").phoneType(PhoneType.celular).build();
        employee1.setPhones(List.of(phone1));

        EmployeeGetResponse employee2 = EmployeeGetResponse.builder().name("Vinicius").lastName("Lima").category(categoryGetResponse).address(addressGetResponse).build();
        PhoneGetResponse phone2 = PhoneGetResponse.builder().number("2222222").phoneType(PhoneType.celular).build();
        employee2.setPhones(List.of(phone2));

        EmployeeGetResponse employee3 = EmployeeGetResponse.builder().name("Vinicius").lastName("Lima").category(categoryGetResponse).address(addressGetResponse).build();
        PhoneGetResponse phone3 = PhoneGetResponse.builder().number("3333333").phoneType(PhoneType.celular).build();
        employee3.setPhones(List.of(phone3));

        return new ArrayList<>(List.of(employee1, employee2, employee3));
    }

    public static EmployeePostRequest newEmployeePostRequest() {
        Address address = AddressUtils.newAddressToSave();

        EmployeePostRequest employee = EmployeePostRequest.builder().name("Chispirito").lastName("Costa").categoryId(4L).address(address).build();
        Phone phone = PhoneUtils.newPhone(4L);
        employee.setPhones(List.of(phone));

        return employee;
    }

    public static Employee newEmployeeToSave() {
        Address address = AddressUtils.newAddressToSave();

        Category category = CategoryUtils.newCategoryToSave();

        Employee employee = Employee.builder().id(4L).name("Chispirito").lastName("Costa").category(category).address(address).build();
        Phone phone = PhoneUtils.newPhone(4L);
        employee.setPhones(List.of(phone));

        return employee;
    }

    public static EmployeePostResponse newEmployeePostResponse() {
        Address address = AddressUtils.newAddressToSave();

        Category category = CategoryUtils.newCategoryToSave();

        EmployeePostResponse employee = EmployeePostResponse.builder().id(4L).name("Chispirito").lastName("Costa").category(category).address(address).build();
        Phone phone = PhoneUtils.newPhone(4L);
        employee.setPhones(List.of(phone));

        return employee;
    }
}
