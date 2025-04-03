package com.branches.service;

import com.branches.mapper.EmployeeMapper;
import com.branches.model.Employee;
import com.branches.repository.EmployeeRepository;
import com.branches.response.EmployeeGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;

    public List<EmployeeGetResponse> findAll(String firstName) {
        List<Employee> response = firstName == null ? repository.findAll() : repository.findByNameContaining(firstName);

        return mapper.toEmployeeGetResponseList(response);
    }
}
