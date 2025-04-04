package com.branches.mapper;

import com.branches.model.Category;
import com.branches.request.CategoryPostRequest;
import com.branches.response.CategoryGetResponse;
import com.branches.response.CategoryPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    List<CategoryGetResponse> toCategoryGetResponseList(List<Category> response);

    CategoryGetResponse toCategoryGetResponse(Category foundCategory);

    @Mapping(target = "id", ignore = true)
    Category toCategory(CategoryPostRequest postRequest);

    CategoryPostResponse toCategoryPostResponse(Category categorySaved);
}
