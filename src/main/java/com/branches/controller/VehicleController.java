package com.branches.controller;

import com.branches.request.VehiclePostRequest;
import com.branches.response.VehicleGetResponse;
import com.branches.response.VehiclePostResponse;
import com.branches.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService service;

    @GetMapping
    public ResponseEntity<List<VehicleGetResponse>> findAll() {
        List<VehicleGetResponse> response = service.findAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleGetResponse> findById(@PathVariable Long id) {
        VehicleGetResponse response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<VehiclePostResponse> save(@Valid @RequestBody VehiclePostRequest postRequest) {
        VehiclePostResponse response = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
