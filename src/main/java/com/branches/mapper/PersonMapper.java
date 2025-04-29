package com.branches.mapper;

import com.branches.model.Person;
import com.branches.request.ClientPostRequest;
import com.branches.request.EmployeePostRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper {
    Person toPerson(ClientPostRequest clientPostRequest);

    Person toPerson(EmployeePostRequest employeePostRequest);
}
