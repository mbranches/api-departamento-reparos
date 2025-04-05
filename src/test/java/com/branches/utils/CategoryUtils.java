package com.branches.utils;

import com.branches.model.Category;
import com.branches.request.CategoryPostRequest;
import com.branches.response.CategoryGetResponse;
import com.branches.response.CategoryPostResponse;

import java.util.ArrayList;
import java.util.List;

public class CategoryUtils {
    public static List<Category> newCategoryList() {
        Category mecanico = Category.builder().id(1L).name("Mecânico").hourlyPrice(50.0).build();
        Category borracheiro = Category.builder().id(2L).name("Borracheiro").hourlyPrice(40.0).build();
        Category pintor = Category.builder().id(3L).name("Pintor").hourlyPrice(30.0).build();

        return new ArrayList<>(List.of(mecanico, borracheiro, pintor));
    }

    public static List<CategoryGetResponse> newCategoryGetResponseList() {
        CategoryGetResponse mecanico = CategoryGetResponse.builder().name("Mecânico").hourlyPrice(50.0).build();
        CategoryGetResponse borracheiro = CategoryGetResponse.builder().name("Borracheiro").hourlyPrice(40.0).build();
        CategoryGetResponse pintor = CategoryGetResponse.builder().name("Pintor").hourlyPrice(30.0).build();

        return new ArrayList<>(List.of(mecanico, borracheiro, pintor));
    }

    public static Category newCategoryToSave() {
        return Category.builder().id(4L).name("Vidraceiro").hourlyPrice(50.0).build();
    }

    public static CategoryPostRequest newCategoryPostRequest() {
        return CategoryPostRequest.builder().name("Vidraceiro").hourlyPrice(50.0).build();
    }

    public static CategoryPostResponse newCategoryPostResponse() {
        return CategoryPostResponse.builder().id(4L).name("Vidraceiro").hourlyPrice(50.0).build();
    }
}
