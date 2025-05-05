package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.mapper.EmployeeMapper;
import com.branches.mapper.PersonMapper;
import com.branches.model.*;
import com.branches.repository.EmployeeRepository;
import com.branches.request.EmployeePostRequest;
import com.branches.request.EmployeePutRequest;
import com.branches.response.EmployeeGetResponse;
import com.branches.response.EmployeePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public EmployeeGetResponse findById(Long id) {
        Employee foundEmployee = findByIdOrThrowsNotFoundException(id);

        return mapper.toEmployeeGetResponse(foundEmployee);
    }

    public Employee findByIdOrThrowsNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not Found"));
    }

    @Transactional
    public EmployeePostResponse save(EmployeePostRequest postRequest) {
        Category category = categoryService.findByIdOrThrowsNotFoundException(postRequest.getCategoryId());

        Person personToSave = personMapper.toPerson(postRequest);

        Person person = personService.save(personToSave);

        Employee employeeToSave = Employee.builder()
                .person(person)
                .category(category)
                .build();

        Employee employee = repository.save(employeeToSave);

        return mapper.toEmployeePostResponse(employee);
    }

    @Transactional
    public void update(Long id, EmployeePutRequest putRequest) {
        if (!id.equals(putRequest.getId())) throw new BadRequestException("The ID in the request body (%s) does not match the ID in the URL (%s)".formatted(putRequest.getId(), id));

        Employee employeeNotUpdated = findByIdOrThrowsNotFoundException(id);

        Category category = categoryService.findByIdOrThrowsNotFoundException(putRequest.getCategoryId());

        Person personNotUpdated = employeeNotUpdated.getPerson();
        Person personToUpdate = personMapper.toPerson(putRequest);
        personToUpdate.setId(personNotUpdated.getId());

        Person personUpdated = personService.update(personToUpdate);

        Employee employeeToUpdate = Employee.builder()
                .id(putRequest.getId())
                .person(personUpdated)
                .category(category)
                .build();

        repository.save(employeeToUpdate);
    }

    public void deleteById(Long id) {
        Employee exceptionToDelete = findByIdOrThrowsNotFoundException(id);

        repository.delete(exceptionToDelete);
    }
}
