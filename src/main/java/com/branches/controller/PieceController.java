package com.branches.controller;

import com.branches.request.PiecePostRequest;
import com.branches.response.PieceGetResponse;
import com.branches.response.PiecePostResponse;
import com.branches.service.PieceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("v1/pieces")
@RequiredArgsConstructor
@RestController
public class PieceController {
    private final PieceService service;

    @GetMapping
    public ResponseEntity<List<PieceGetResponse>> findAll(@RequestParam(required = false) String name) {
        List<PieceGetResponse> response = service.findAll(name);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PieceGetResponse> findById(@PathVariable Long id) {
        PieceGetResponse response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PiecePostResponse> save(@Valid @RequestBody PiecePostRequest postRequest) {
        PiecePostResponse response = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
