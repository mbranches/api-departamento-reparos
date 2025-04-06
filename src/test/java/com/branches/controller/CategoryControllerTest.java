package com.branches.controller;

import com.branches.exception.NotFoundException;
import com.branches.request.CategoryPostRequest;
import com.branches.response.CategoryGetResponse;
import com.branches.service.CategoryService;
import com.branches.utils.CategoryUtils;
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

@WebMvcTest(controllers = CategoryController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(FileUtils.class)
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CategoryService service;
    @Autowired
    private FileUtils fileUtils;
    private final String URL = "/v1/categories";
    private List<CategoryGetResponse> categoryGetResponseList;

    @BeforeEach
    void init() {
        categoryGetResponseList = CategoryUtils.newCategoryGetResponseList();
    }

    @Test
    @DisplayName("GET /v1/categories returns all categories when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllCategories_WhenGivenArgumentIsNull() throws Exception {
        BDDMockito.when(service.findAll(null)).thenReturn(categoryGetResponseList);
        String expectedResponse = fileUtils.readResourceFile("category/get-category-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/categories?name=mecânico returns the found categories when the argument is found")
    @Order(2)
    void findAll_ReturnsFoundCategories_WhenArgumentIsGiven() throws Exception {
        String nameToSearch = "mecânico";

        BDDMockito.when(service.findAll(nameToSearch)).thenReturn(List.of(categoryGetResponseList.getFirst()));
        String expectedResponse = fileUtils.readResourceFile("category/get-category-mecanico-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", nameToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }



    @Test
    @DisplayName("GET /v1/categories?nameNotRegistered returns an empty list when the given argument is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenGivenArgumentIsNotFound() throws Exception {
        String randomName = "nameNotRegistered";

        BDDMockito.when(service.findAll(randomName)).thenReturn(Collections.emptyList());

        String expectedResponse = fileUtils.readResourceFile("category/get-category-nameNotRegistered-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", randomName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/categories/1 returns found Category when successful")
    @Order(4)
    void findById_ReturnsFoundCategory_WhenSuccessful() throws Exception {
        CategoryGetResponse expectedCategory = categoryGetResponseList.getFirst();
        long idToSearch = 1L;

        BDDMockito.when(service.findById(idToSearch)).thenReturn(expectedCategory);
        String expectedResponse = fileUtils.readResourceFile("category/get-category-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", idToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/categories/131222 throws NotFoundException when id is not found")
    @Order(5)
    void findById_ThrowsNotFoundException_WhenIdIsNotFound() throws Exception {
        long randomId = 131222L;

        BDDMockito.when(service.findById(randomId)).thenThrow(new NotFoundException("Category not Found"));
        String expectedResponse = fileUtils.readResourceFile("category/get-category-by-id-404.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/categories returns saved category when successful")
    @Order(6)
    void save_ReturnsSavedCategory_WhenSuccessful() throws Exception {
        BDDMockito.when(service.save(ArgumentMatchers.any(CategoryPostRequest.class))).thenReturn(CategoryUtils.newCategoryPostResponse());
        String request = fileUtils.readResourceFile("category/post-request-category-200.json");
        String expectedResponse = fileUtils.readResourceFile("category/post-response-category-201.json");

        mockMvc.perform(
                    MockMvcRequestBuilders.post(URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
}
