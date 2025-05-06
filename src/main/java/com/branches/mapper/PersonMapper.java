package com.branches.mapper;

import com.branches.model.Person;
import com.branches.request.ClientPostRequest;
import com.branches.request.ClientPutRequest;
import com.branches.request.EmployeePostRequest;
import com.branches.request.EmployeePutRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper {
    @Mapping(target = "id", ignore = true)
    Person toPerson(ClientPostRequest clientPostRequest);

    @Mapping(target = "id", ignore = true)
    Person toPerson(EmployeePostRequest employeePostRequest);

    @Mapping(target = "id", ignore = true)
    Person toPerson(ClientPutRequest putRequest);

    @Mapping(target = "id", ignore = true)
    Person toPerson(EmployeePutRequest putRequest);
}
