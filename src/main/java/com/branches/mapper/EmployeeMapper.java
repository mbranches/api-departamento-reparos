package com.branches.mapper;

import com.branches.model.Employee;
import com.branches.request.EmployeePostRequest;
import com.branches.response.EmployeeGetResponse;
import com.branches.response.EmployeePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {
    List<EmployeeGetResponse> toEmployeeGetResponseList(List<Employee> employeeList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Employee toEmployee(EmployeePostRequest postRequest);

    EmployeePostResponse toEmployeePostResponse(Employee employee);

    EmployeeGetResponse toEmployeeGetResponse(Employee foundEmployee);
}
