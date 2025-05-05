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
import com.branches.utils.CategoryUtils;
import com.branches.utils.EmployeeUtils;
import com.branches.utils.PersonUtils;
import jakarta.validation.constraints.NotNull;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeServiceTest {
    @InjectMocks
    private EmployeeService service;
    @Mock
    private EmployeeRepository repository;
    @Mock
    private AddressService addressService;
    @Mock
    private PhoneService phoneService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private PersonService personService;
    @Mock
    private PersonMapper personMapper;
    @Mock
    private EmployeeMapper mapper;
    private List<EmployeeGetResponse> employeeGetResponseList;
    private List<Employee> employeeList;

    @BeforeEach
    void init() {
        employeeGetResponseList = EmployeeUtils.newEmployeeGetResponseList();
        employeeList = EmployeeUtils.newEmployeeList();
    }

    @Test
    @DisplayName("findAll returns all employees when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllEmployees_WhenGivenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(employeeList);
        BDDMockito.when(mapper.toEmployeeGetResponseList(ArgumentMatchers.anyList())).thenReturn(employeeGetResponseList);

        List<EmployeeGetResponse> response = service.findAll(null);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(employeeGetResponseList);
    }

    @Test
    @DisplayName("findAll returns the found employees when the argument is given")
    @Order(2)
    void findAll_ReturnsFoundEmployees_WhenArgumentIsGiven() {
        EmployeeGetResponse employeeToBeFound = employeeGetResponseList.getFirst();
        String nameToSearch = employeeToBeFound.getPerson().getName();
        List<EmployeeGetResponse> expectedResponse = List.of(employeeToBeFound);

        List<Employee> expectedResponseRepository = List.of(employeeList.getFirst());

        BDDMockito.when(repository.findAllByPerson_NameContaining(nameToSearch)).thenReturn(expectedResponseRepository);
        BDDMockito.when(mapper.toEmployeeGetResponseList(ArgumentMatchers.anyList())).thenReturn(expectedResponse);

        List<EmployeeGetResponse> response = service.findAll(nameToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findAll returns an empty list when the given argument is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenGivenArgumentIsNotFound() {
        String randomName = "name invalid";

        BDDMockito.when(repository.findAllByPerson_NameContaining(randomName)).thenReturn(Collections.emptyList());
        BDDMockito.when(mapper.toEmployeeGetResponseList(ArgumentMatchers.anyList())).thenReturn(Collections.emptyList());

        List<EmployeeGetResponse> response = service.findAll(randomName);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns found employee when successful")
    @Order(4)
    void findById_ReturnsFoundEmployee_WhenSuccessful() {
        Employee expectedResponseRepository = employeeList.getFirst();
        Long idToSearch = expectedResponseRepository.getId();

        EmployeeGetResponse expectedResponse = employeeGetResponseList.getFirst();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedResponseRepository));
        BDDMockito.when(mapper.toEmployeeGetResponse(expectedResponseRepository)).thenReturn(expectedResponse);

        EmployeeGetResponse response = service.findById(idToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("findById throws NotFoundException when id is not found")
    @Order(5)
    void findById_ThrowsNotFoundException_WhenIdIsNotFound() {
        Long randomId = 4445511L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Employee not Found");
    }
    
    @Test
    @DisplayName("save returns saved employee when successful")
    @Order(6)
    void save_ReturnsSavedEmployee_WhenSuccessful() {
        Person personToSave = PersonUtils.newPersonToSave();
        Person personSaved = PersonUtils.newPersonSaved();

        Employee employeeToSave = EmployeeUtils.newEmployeeToSave();
        Employee employeeSaved = EmployeeUtils.newEmployeeSaved();

        EmployeePostRequest employeePostRequest = EmployeeUtils.newEmployeePostRequest();
        EmployeePostResponse expectedResponse = EmployeeUtils.newEmployeePostResponse();

        Category category = CategoryUtils.newCategoryList().getFirst();
        BDDMockito.when(categoryService.findByIdOrThrowsNotFoundException(employeePostRequest.getCategoryId())).thenReturn(category);
        BDDMockito.when(personMapper.toPerson(employeePostRequest)).thenReturn(personToSave);
        BDDMockito.when(personService.save(personToSave)).thenReturn(personSaved);
        BDDMockito.when(repository.save(employeeToSave)).thenReturn(employeeSaved);
        BDDMockito.when(mapper.toEmployeePostResponse(employeeSaved)).thenReturn(expectedResponse);

        EmployeePostResponse response = service.save(employeePostRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws NotFoundException when given category does not exists")
    @Order(7)
    void save_ThrowsNotFoundException_WhenGivenCategoryNotExists() {
        EmployeePostRequest employeePostRequest = EmployeeUtils.newEmployeePostRequest();

        BDDMockito.when(categoryService.findByIdOrThrowsNotFoundException(ArgumentMatchers.anyLong())).thenThrow(new NotFoundException("Category not Found"));

        Assertions.assertThatThrownBy(() -> service.save(employeePostRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category not Found");
    }

    @Test
    @DisplayName("save throws BadRequestException when the phone already exists")
    @Order(8)
    void save_ThrowsBadRequestException_WhenThePhoneAlreadyExists() {
        Person personToSave = PersonUtils.newPersonToSave().withId(null);
        EmployeePostRequest employeePostRequest = EmployeeUtils.newEmployeePostRequest();

        Category category = CategoryUtils.newCategoryList().getFirst();
        BDDMockito.when(categoryService.findByIdOrThrowsNotFoundException(employeePostRequest.getCategoryId())).thenReturn(category);
        BDDMockito.when(personMapper.toPerson(employeePostRequest)).thenReturn(personToSave);
        BDDMockito.doThrow(BadRequestException.class).when(personService).save(personToSave);

        Assertions.assertThatThrownBy(() -> service.save(employeePostRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("update updates employee when successful")
    @Order(9)
    void update_UpdatesEmployee_WhenSuccessful() {
        Employee employeeNotUpdated = employeeList.getFirst();

        Employee employeeToUpdate = EmployeeUtils.newEmployeeToUpdate();
        EmployeePutRequest putRequest = EmployeeUtils.newEmployeePutRequest();

        Person personToUpdate = PersonUtils.newPersonToUpdate();

        Category categoryToBeFound = CategoryUtils.newCategoryList().get(1);

        BDDMockito.when(repository.findById(putRequest.getId())).thenReturn(Optional.of(employeeNotUpdated));
        BDDMockito.when(categoryService.findByIdOrThrowsNotFoundException(putRequest.getCategoryId())).thenReturn(categoryToBeFound);
        BDDMockito.when(personMapper.toPerson(putRequest)).thenReturn(personToUpdate.withId(null));
        BDDMockito.when(personService.update(personToUpdate)).thenReturn(personToUpdate);
        BDDMockito.when(repository.save(employeeToUpdate)).thenReturn(employeeToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(putRequest.getId(), putRequest));
    }

    @Test
    @DisplayName("update throws BadRequestException when the url id does not match the request body id")
    @Order(10)
    void update_ThrowsBadRequestException_WhenTheUrlIdDoesNotMatchTheRequestBodyId () {
        EmployeePutRequest putRequest = EmployeeUtils.newEmployeePutRequest();
        Long randomId = 999L;

        Assertions.assertThatThrownBy(() -> service.update(randomId, putRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("The ID in the request body (%s) does not match the ID in the URL (%s)".formatted(putRequest.getId(), randomId));
    }

    @Test
    @DisplayName("update throws NotFoundException when employee is not found")
    @Order(11)
    void update_ThrowsNotFoundException_WhenEmployeeIsNotFound() {
        EmployeePutRequest putRequest = EmployeeUtils.newEmployeePutRequest();

        BDDMockito.when(repository.findById(putRequest.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.update(putRequest.getId(), putRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Employee not Found");

    }

    @Test
    @DisplayName("update throws NotFoundException when the category is not found")
    @Order(12)
    void update_ThrowsNotFoundException_WhenTheCategoryIsNotFound() {
        Employee employeeNotUpdated = employeeList.getFirst();
        Long randomCategoryId = 999L;
        EmployeePutRequest putRequest = EmployeeUtils.newEmployeePutRequest().withCategoryId(randomCategoryId);

        BDDMockito.when(repository.findById(putRequest.getId())).thenReturn(Optional.of(employeeNotUpdated));
        BDDMockito.doThrow(NotFoundException.class).when(categoryService).findByIdOrThrowsNotFoundException(putRequest.getCategoryId());

        Assertions.assertThatThrownBy(() -> service.update(putRequest.getId(), putRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteById removes employee when successful")
    @Order(13)
    void deleteById_RemovesEmployee_WhenSuccessful() {
        Employee employeeToDelete = employeeList.getFirst();
        Long idToDelete = employeeToDelete.getId();

        BDDMockito.when(repository.findById(idToDelete)).thenReturn(Optional.of(employeeToDelete));
        BDDMockito.doNothing().when(repository).delete(ArgumentMatchers.any(Employee.class));

        Assertions.assertThatCode(() -> service.deleteById(idToDelete))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteById throws NotFoundException when given id is not found")
    @Order(14)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Employee not Found");
    }
}