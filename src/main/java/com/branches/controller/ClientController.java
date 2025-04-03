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
    private final ClientMapper mapper;

    @GetMapping
    public ResponseEntity<List<ClientGetResponse>> findAll(@RequestParam(required = false) String firstName) {
        List<Client> clients = service.findAll(firstName);

        List<ClientGetResponse> response = mapper.toClientGetResponseList(clients);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientGetResponse> findById(@PathVariable Long id) {
        Client client = service.findByIdOrElseThrowsNotFoundException(id);

        ClientGetResponse response = mapper.toClientGetResponse(client);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ClientPostResponse> save(@RequestBody ClientPostRequest postRequest) {
        Client clientToSave = mapper.toClient(postRequest);

        Client clientSaved = service.save(clientToSave);

        ClientPostResponse response = mapper.toClientPostResponse(clientSaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
