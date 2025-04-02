package com.branches.controller;

import com.branches.mapper.ClientMapper;
import com.branches.model.Client;
import com.branches.response.ClientGetResponse;
import com.branches.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequestMapping("v1/clients")
@RestController
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;
    private final ClientMapper mapper;

    @GetMapping
    public ResponseEntity<List<ClientGetResponse>> findAll(@RequestParam(required = false) String firstName) {
        List<Client> clients = service.findAll(firstName);

        List<ClientGetResponse> response = mapper.toClientGetResponseList(clients);

        return ResponseEntity.ok(response);
    }
}
