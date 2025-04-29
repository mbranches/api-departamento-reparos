package com.branches.controller;

import com.branches.request.ClientPostRequest;
import com.branches.response.ClientGetResponse;
import com.branches.response.ClientPostResponse;
import com.branches.response.RepairGetResponse;
import com.branches.response.VehicleDefaultResponse;
import com.branches.service.ClientService;
import com.branches.service.RepairService;
import com.branches.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("v1/clients")
@RestController
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;
    private final VehicleService vehicleService;
    private final RepairService repairService;

    @GetMapping
    public ResponseEntity<List<ClientGetResponse>> findAll(@RequestParam(required = false) String firstName) {
        List<ClientGetResponse> response = service.findAll(firstName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientGetResponse> findById(@PathVariable Long id) {
        ClientGetResponse response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clientId}/vehicles")
    public ResponseEntity<List<VehicleDefaultResponse>> findVehiclesByClientId(@PathVariable Long clientId) {
        List<VehicleDefaultResponse> response = vehicleService.findByClientId(clientId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clientId}/repairs")
    public ResponseEntity<List<RepairGetResponse>> findRepairsByClientId(@PathVariable Long clientId) {
        List<RepairGetResponse> response = repairService.findAllByClientId(clientId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ClientPostResponse> save(@Valid @RequestBody ClientPostRequest postRequest) {
        ClientPostResponse response = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
