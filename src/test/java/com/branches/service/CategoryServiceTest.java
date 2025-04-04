package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.model.Category;
import com.branches.repository.CategoryRepository;
import com.branches.utils.CategoryUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryServiceTest {
    @InjectMocks
    private CategoryService service;
    @Mock
    private CategoryRepository repository;
    private List<Category> categoryList;

    @BeforeEach
    void init() {
        categoryList = CategoryUtils.newCategoryList();
    }

    @Test
    @DisplayName("findByIdOrElseThrowsNotFoundException returns found category when successful")
    @Order(1)
    void findByIdOrElseThrowsNotFoundException_ReturnsFoundCategory_WhenSuccessful() {
        Category expectedResponse = categoryList.getFirst();
        Long idToSearch = expectedResponse.getId();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedResponse));

        Category response = service.findByIdOrElseThrowsNotFoundException(idToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("findByIdOrElseThrowsNotFoundException throws NotFoundException when category is not found")
    @Order(2)
    void findByIdOrElseThrowsNotFoundException_ThrowsNotFoundException_WhenCategoryIsNotFound() {
        long randomId = 1213213L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findByIdOrElseThrowsNotFoundException(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category not Found");
    }
}