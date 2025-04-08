package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.EmployeeMapper;
import com.branches.model.Address;
import com.branches.model.Category;
import com.branches.model.Employee;
import com.branches.model.Phone;
import com.branches.repository.EmployeeRepository;
import com.branches.request.EmployeePostRequest;
import com.branches.response.EmployeeGetResponse;
import com.branches.response.EmployeePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public EmployeePostResponse save(EmployeePostRequest postRequest) {
        Category category = categoryService.findByIdOrThrowsNotFoundException(postRequest.getCategoryId());

        Employee employeeToSave = mapper.toEmployee(postRequest);
        employeeToSave.setCategory(category);

        Address address = employeeToSave.getAddress();
        if (postRequest.getAddress() != null) {
            Optional<Address> addressSearched = addressService.findAddress(address);

            Address addressSaved = addressSearched.orElseGet(() -> addressService.save(address));
            employeeToSave.setAddress(addressSaved);
        }

        List<Phone> phones = employeeToSave.getPhones();
        if (phones != null) phones.forEach(phone -> phone.setEmployee(employeeToSave));

        Employee response = repository.save(employeeToSave);

        return mapper.toEmployeePostResponse(response);
    }

    public EmployeeGetResponse findById(Long id) {
        Employee foundEmployee = findByIdOrNotFoundException(id);

        return mapper.toEmployeeGetResponse(foundEmployee);
    }

    private Employee findByIdOrNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not Found"));
    }
}
