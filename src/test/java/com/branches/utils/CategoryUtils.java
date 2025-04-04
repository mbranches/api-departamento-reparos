package com.branches.utils;

import com.branches.model.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryUtils {
    public List<Category> newCategoryList() {
        Category mecanico = Category.builder().id(1L).name("Mecânico").hourlyPrice(50.0).build();
        Category borracheiro = Category.builder().id(2L).name("Borracheiro").hourlyPrice(40.0).build();
        Category pintor = Category.builder().id(3L).name("Pintor").hourlyPrice(30.0).build();

        return new ArrayList<>(List.of(mecanico, borracheiro, pintor));
    }
}
