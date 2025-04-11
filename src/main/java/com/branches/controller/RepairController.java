package com.branches.controller;

import com.branches.request.RepairPostRequest;
import com.branches.response.RepairGetResponse;
import com.branches.response.RepairPostResponse;
import com.branches.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/repairs")
@RequiredArgsConstructor
public class RepairController {
    private final RepairService service;

    @GetMapping
    public ResponseEntity<List<RepairGetResponse>> findAll() {
        List<RepairGetResponse> response = service.findAll();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RepairPostResponse> save(@RequestBody RepairPostRequest postRequest) {
        RepairPostResponse response = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
