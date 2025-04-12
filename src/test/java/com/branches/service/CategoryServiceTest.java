package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.CategoryMapper;
import com.branches.model.Category;
import com.branches.repository.CategoryRepository;
import com.branches.request.CategoryPostRequest;
import com.branches.response.CategoryGetResponse;
import com.branches.response.CategoryPostResponse;
import com.branches.utils.CategoryUtils;
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
class CategoryServiceTest {
    @InjectMocks
    private CategoryService service;
    @Mock
    private CategoryRepository repository;
    @Mock
    private CategoryMapper mapper;
    private List<Category> categoryList;
    private List<CategoryGetResponse> categoryGetResponseList;

    @BeforeEach
    void init() {
        categoryList = CategoryUtils.newCategoryList();
        categoryGetResponseList = CategoryUtils.newCategoryGetResponseList();
    }

    @Test
    @DisplayName("findAll returns all categories when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllCategories_WhenGivenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(categoryList);
        BDDMockito.when(mapper.toCategoryGetResponseList(ArgumentMatchers.anyList())).thenReturn(categoryGetResponseList);

        List<CategoryGetResponse> response = service.findAll(null);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(categoryGetResponseList);
    }

    @Test
    @DisplayName("findAll returns the found categories when the argument is given")
    @Order(2)
    void findAll_ReturnsFoundCategories_WhenArgumentIsGiven() {
        CategoryGetResponse categoryToBeFound = categoryGetResponseList.getFirst();
        String nameToSearch = categoryToBeFound.getName();
        List<CategoryGetResponse> expectedResponse = List.of(categoryToBeFound);

        List<Category> expectedResponseRepository = List.of(categoryList.getFirst());

        BDDMockito.when(repository.findAllByNameContaining(nameToSearch)).thenReturn(expectedResponseRepository);
        BDDMockito.when(mapper.toCategoryGetResponseList(ArgumentMatchers.anyList())).thenReturn(expectedResponse);

        List<CategoryGetResponse> response = service.findAll(nameToSearch);

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

        BDDMockito.when(repository.findAllByNameContaining(randomName)).thenReturn(Collections.emptyList());
        BDDMockito.when(mapper.toCategoryGetResponseList(ArgumentMatchers.anyList())).thenReturn(Collections.emptyList());

        List<CategoryGetResponse> response = service.findAll(randomName);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns found Category when successful")
    @Order(4)
    void findById_ReturnsFoundCategory_WhenSuccessful() {
        Category expectedResponseRepository = categoryList.getFirst();
        Long idToSearch = expectedResponseRepository.getId();

        CategoryGetResponse expectedResponse = categoryGetResponseList.getFirst();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedResponseRepository));
        BDDMockito.when(mapper.toCategoryGetResponse(expectedResponseRepository)).thenReturn(expectedResponse);

        CategoryGetResponse response = service.findById(idToSearch);

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
                .hasMessageContaining("Category not Found");
    }

    @Test
    @DisplayName("save returns saved category when successful")
    @Order(6)
    void save_ReturnsSavedCategory_WhenSuccessful() {
        Category CategoryToSave = CategoryUtils.newCategoryToSave();
        CategoryPostRequest CategoryPostRequest = CategoryUtils.newCategoryPostRequest();

        CategoryPostResponse expectedResponse = CategoryUtils.newCategoryPostResponse();

        BDDMockito.when(mapper.toCategory(CategoryPostRequest)).thenReturn(CategoryToSave);
        BDDMockito.when(repository.save(CategoryToSave)).thenReturn(CategoryToSave);
        BDDMockito.when(mapper.toCategoryPostResponse(CategoryToSave)).thenReturn(expectedResponse);

        CategoryPostResponse response = service.save(CategoryPostRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("deleteById removes category when successful")
    @Order(7)
    void deleteById_RemovesCategory_WhenSuccessful() {
        Category categoryToDelete = categoryList.getFirst();
        Long idToDelete = categoryToDelete.getId();

        BDDMockito.when(repository.findById(idToDelete)).thenReturn(Optional.of(categoryToDelete));
        BDDMockito.doNothing().when(repository).delete(ArgumentMatchers.any(Category.class));

        Assertions.assertThatCode(() -> service.deleteById(idToDelete))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteById throws NotFoundException when given id is not found")
    @Order(8)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category not Found");
    }
}