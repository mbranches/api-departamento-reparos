package com.branches.mapper;

import com.branches.model.Employee;
import com.branches.response.EmployeeGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {
    List<EmployeeGetResponse> toEmployeeGetResponseList(List<Employee> employeeList);
}
