package com.branches.controller;

import com.branches.mapper.VehicleMapper;
import com.branches.model.Vehicle;
import com.branches.response.VehicleGetResponse;
import com.branches.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
