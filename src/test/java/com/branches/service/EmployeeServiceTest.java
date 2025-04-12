package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.EmployeeMapper;
import com.branches.model.Address;
import com.branches.model.Employee;
import com.branches.repository.EmployeeRepository;
import com.branches.request.EmployeePostRequest;
import com.branches.response.EmployeeGetResponse;
import com.branches.response.EmployeePostResponse;
import com.branches.utils.CategoryUtils;
import com.branches.utils.EmployeeUtils;
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
    private CategoryService categoryService;
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
        String nameToSearch = employeeToBeFound.getName();
        List<EmployeeGetResponse> expectedResponse = List.of(employeeToBeFound);

        List<Employee> expectedResponseRepository = List.of(employeeList.getFirst());

        BDDMockito.when(repository.findByNameContaining(nameToSearch)).thenReturn(expectedResponseRepository);
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

        BDDMockito.when(repository.findByNameContaining(randomName)).thenReturn(Collections.emptyList());
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
        Employee employeeToSave = EmployeeUtils.newEmployeeToSave();
        EmployeePostRequest employeePostRequest = EmployeeUtils.newEmployeePostRequest();

        Address employeeAddress = employeeToSave.getAddress();

        EmployeePostResponse expectedResponse = EmployeeUtils.newEmployeePostResponse();

        BDDMockito.when(categoryService.findByIdOrThrowsNotFoundException(employeePostRequest.getCategoryId())).thenReturn(CategoryUtils.newCategoryToSave());
        BDDMockito.when(mapper.toEmployee(employeePostRequest)).thenReturn(employeeToSave);
        BDDMockito.when(addressService.findAddress(employeeAddress)).thenReturn(Optional.of(employeeAddress));
        BDDMockito.when(repository.save(employeeToSave)).thenReturn(employeeToSave);
        BDDMockito.when(mapper.toEmployeePostResponse(employeeToSave)).thenReturn(expectedResponse);

        EmployeePostResponse response = service.save(employeePostRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws not found exception when given category does not exists")
    @Order(7)
    void save_ThrowsNotFoundException_WhenGivenCategoryNotExists() {
        EmployeePostRequest employeePostRequest = EmployeeUtils.newEmployeePostRequest();

        BDDMockito.when(categoryService.findByIdOrThrowsNotFoundException(ArgumentMatchers.anyLong())).thenThrow(new NotFoundException("Category not Found"));

        Assertions.assertThatThrownBy(() -> service.save(employeePostRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category not Found");
    }

    @Test
    @DisplayName("deleteById removes employee when successful")
    @Order(8)
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
    @Order(9)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Employee not Found");
    }
}