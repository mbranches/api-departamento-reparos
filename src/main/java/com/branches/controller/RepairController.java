package com.branches.controller;

import com.branches.request.RepairPostRequest;
import com.branches.response.RepairPostResponse;
import com.branches.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/repairs")
@RequiredArgsConstructor
public class RepairController {
    private final RepairService service;

    @PostMapping
    public ResponseEntity<RepairPostResponse> save(@RequestBody RepairPostRequest postRequest) {
        RepairPostResponse response = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
