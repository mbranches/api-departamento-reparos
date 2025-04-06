package com.branches.controller;

import com.branches.exception.NotFoundException;
import com.branches.request.EmployeePostRequest;
import com.branches.response.EmployeeGetResponse;
import com.branches.service.EmployeeService;
import com.branches.utils.EmployeeUtils;
import com.branches.utils.FileUtils;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

@WebMvcTest(controllers = EmployeeController.class)
@Import(FileUtils.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EmployeeService service;
    @Autowired
    private FileUtils fileUtils;
    private final String URL = "/v1/employees";
    private List<EmployeeGetResponse> employeeGetResponseList;

    @BeforeEach
    void init() {
        employeeGetResponseList = EmployeeUtils.newEmployeeGetResponseList();
    }

    @Test
    @DisplayName("GET /v1/employees returns all employees when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllEmployees_WhenGivenArgumentIsNull() throws Exception {
        BDDMockito.when(service.findAll(null)).thenReturn(employeeGetResponseList);
        String expectedResponse = fileUtils.readResourceFile("employee/get-employees-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/employees returns the found employees when the argument is given")
    @Order(2)
    void findAll_ReturnsFoundEmployees_WhenArgumentIsGiven() throws Exception {
        String nameToSearch = "Marcus";
        BDDMockito.when(service.findAll(nameToSearch)).thenReturn(List.of(employeeGetResponseList.getFirst()));
        String expectedResponse = fileUtils.readResourceFile("employee/get-employees-marcus-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", nameToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/employees returns an empty list when the given argument is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenGivenArgumentIsNotFound() throws Exception {
        String randomName = "nameNotRegistered";
        BDDMockito.when(service.findAll(randomName)).thenReturn(Collections.emptyList());
        String expectedResponse = fileUtils.readResourceFile("employee/get-employees-nameNotRegistered-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", randomName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/employees returns saved employee when successful")
    @Order(4)
    void save_ReturnsSavedEmployee_WhenGivenSuccessful() throws Exception {
        BDDMockito.when(service.save(ArgumentMatchers.any(EmployeePostRequest.class))).thenReturn(EmployeeUtils.newEmployeePostResponse());

        String request = fileUtils.readResourceFile("employee/post-request-employee-valid-category-200.json");
        String expectedResponse = fileUtils.readResourceFile("employee/post-response-employee-201.json");

        mockMvc.perform(
                    MockMvcRequestBuilders.post(URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("save throws not found exception when given category does not exists")
    @Order(5)
    void save_ThrowsNotFoundException_WhenGivenCategoryNotExists() throws Exception {

        BDDMockito.when(service.save(ArgumentMatchers.any(EmployeePostRequest.class))).thenThrow(new NotFoundException("Category not Found"));

        String request = fileUtils.readResourceFile("employee/post-request-employee-invalid-category-200.json");
        String expectedResponse = fileUtils.readResourceFile("employee/post-response-employee-404.json");

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
}