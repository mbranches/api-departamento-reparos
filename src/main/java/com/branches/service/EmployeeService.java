package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.EmployeeMapper;
import com.branches.mapper.PersonMapper;
import com.branches.model.*;
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
    private final PersonMapper personMapper;
    private final CategoryService categoryService;
    private final AddressService addressService;
    private final PhoneService phoneService;
    private final PersonService personService;

    public List<EmployeeGetResponse> findAll(String firstName) {
        List<Employee> response = firstName == null ? repository.findAll() : repository.findAllByPerson_NameContaining(firstName);

        return mapper.toEmployeeGetResponseList(response);
    }

    @Transactional
    public EmployeePostResponse save(EmployeePostRequest postRequest) {
        Category category = categoryService.findByIdOrThrowsNotFoundException(postRequest.getCategoryId());

        Person personToSave = personMapper.toPerson(postRequest);

        Address address = personToSave.getAddress();
        if (postRequest.getAddress() != null) {
            Optional<Address> addressSearched = addressService.findAddress(address);

            Address addressSaved = addressSearched.orElseGet(() -> addressService.save(address));
            personToSave.setAddress(addressSaved);
        }

        List<Phone> phones = personToSave.getPhones();
        if (phones != null) phones.forEach(phone -> {
            phoneService.assertPhoneDoesNotExists(phone);
            phone.setPerson(personToSave);
        });

        Person person = personService.save(personToSave);

        Employee employeeToSave = Employee.builder().person(person).category(category).build();

        Employee employee = repository.save(employeeToSave);

        return mapper.toEmployeePostResponse(employee);
    }

    public EmployeeGetResponse findById(Long id) {
        Employee foundEmployee = findByIdOrThrowsNotFoundException(id);

        return mapper.toEmployeeGetResponse(foundEmployee);
    }

    public Employee findByIdOrThrowsNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not Found"));
    }

    public void deleteById(Long id) {
        Employee exceptionToDelete = findByIdOrThrowsNotFoundException(id);

        repository.delete(exceptionToDelete);
    }
}
