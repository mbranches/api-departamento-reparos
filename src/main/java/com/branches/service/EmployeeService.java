package com.branches.service;

import com.branches.model.Employee;
import com.branches.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;

    public List<Employee> findAll(String firstName) {
        return firstName == null ? repository.findAll() : repository.findByNameContaining(firstName);
    }
}
