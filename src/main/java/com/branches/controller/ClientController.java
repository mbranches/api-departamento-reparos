package com.branches.controller;

import com.branches.mapper.ClientMapper;
import com.branches.model.Client;
import com.branches.request.ClientPostRequest;
import com.branches.response.ClientGetResponse;
import com.branches.response.ClientPostResponse;
import com.branches.service.ClientService;
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

    @PostMapping
    public ResponseEntity<ClientPostResponse> save(@RequestBody ClientPostRequest postRequest) {
        ClientPostResponse response = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
