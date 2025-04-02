package com.branches.controller;

import com.branches.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService service;
}
