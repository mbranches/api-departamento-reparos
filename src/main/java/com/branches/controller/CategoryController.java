package com.branches.controller;

import com.branches.request.CategoryPostRequest;
import com.branches.response.CategoryGetResponse;
import com.branches.response.CategoryPostResponse;
import com.branches.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("v1/categories")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService service;

    @GetMapping
    public ResponseEntity<List<CategoryGetResponse>> findAll(@RequestParam(required = false) String name) {
        List<CategoryGetResponse> response = service.findAll(name);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryGetResponse> findById(@PathVariable Long id) {
        CategoryGetResponse response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryPostResponse> save(@RequestBody CategoryPostRequest postRequest) {
        CategoryPostResponse response = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
