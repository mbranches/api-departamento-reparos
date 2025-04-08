package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.CategoryMapper;
import com.branches.model.Category;
import com.branches.repository.CategoryRepository;
import com.branches.request.CategoryPostRequest;
import com.branches.response.CategoryGetResponse;
import com.branches.response.CategoryPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public Category findByIdOrThrowsNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not Found"));
    }

    public List<CategoryGetResponse> findAll(String name) {
        List<Category> response = name == null ? repository.findAll() : repository.findAllByNameContaining((name));

        return mapper.toCategoryGetResponseList(response);
    }

    public CategoryGetResponse findById(Long id) {
        Category foundCategory = findByIdOrThrowsNotFoundException(id);

        return mapper.toCategoryGetResponse(foundCategory);
    }

    public CategoryPostResponse save(CategoryPostRequest postRequest) {
        Category categoryToSave = mapper.toCategory(postRequest);

        Category categorySaved = repository.save(categoryToSave);

        return mapper.toCategoryPostResponse(categorySaved);
    }
}
