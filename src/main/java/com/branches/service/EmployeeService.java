package com.branches.service;

import com.branches.mapper.EmployeeMapper;
import com.branches.model.Address;
import com.branches.model.Category;
import com.branches.model.Employee;
import com.branches.repository.EmployeeRepository;
import com.branches.request.EmployeePostRequest;
import com.branches.response.EmployeeGetResponse;
import com.branches.response.EmployeePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;
    private final CategoryService categoryService;
    private final AddressService addressService;

    public List<EmployeeGetResponse> findAll(String firstName) {
        List<Employee> response = firstName == null ? repository.findAll() : repository.findByNameContaining(firstName);

        return mapper.toEmployeeGetResponseList(response);
    }

    public EmployeePostResponse save(EmployeePostRequest postRequest) {
        Category category = categoryService.findByIdOrElseThrowsNotFoundException(postRequest.getCategoryId());

        Employee employeeToSave = mapper.toEmployee(postRequest);
        employeeToSave.setCategory(category);

        Address address = employeeToSave.getAddress();

        Optional<Address> addressSearched = addressService.findAddress(address);
        Address addressSaved = addressSearched.orElseGet(() -> addressService.save(address));
        employeeToSave.setAddress(addressSaved);

        employeeToSave.getPhones()
                .forEach(phone -> phone.setEmployee(employeeToSave));

        Employee response = repository.save(employeeToSave);

        return mapper.toEmployeePostResponse(response);
    }
}
