package com.branches.controller;

import com.branches.request.RepairEmployeePostRequest;
import com.branches.request.RepairPiecePostRequest;
import com.branches.request.RepairPostRequest;
import com.branches.response.*;
import com.branches.service.RepairEmployeeService;
import com.branches.service.RepairPieceService;
import com.branches.service.RepairService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("v1/repairs")
@RequiredArgsConstructor
public class RepairController {
    private final RepairService service;
    private final RepairEmployeeService repairEmployeeService;
    private final RepairPieceService repairPieceService;

    @GetMapping
    public ResponseEntity<List<RepairGetResponse>> findAll(@RequestParam(required = false) LocalDate dateRepair) {
        List<RepairGetResponse> response = service.findAll(dateRepair);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairGetResponse> findById(@PathVariable Long id) {
        RepairGetResponse response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{repairId}/employees")
    public ResponseEntity<List<RepairEmployeePostResponse>> findEmployeesByRepairId(@PathVariable Long repairId){
        List<RepairEmployeePostResponse> response = repairEmployeeService.findAllByRepairId(repairId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{repairId}/pieces")
    public ResponseEntity<List<RepairPiecePostResponse>> findPiecesByRepairId(@PathVariable Long repairId){
        List<RepairPiecePostResponse> response = repairPieceService.findAllByRepairId(repairId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RepairPostResponse> save(@Valid @RequestBody RepairPostRequest postRequest) {
        RepairPostResponse response = service.save(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{repairId}/employees")
    public ResponseEntity<RepairEmployeePostResponse> addEmployee(@PathVariable Long repairId, @Valid @RequestBody RepairEmployeePostRequest postRequest) {
        RepairEmployeePostResponse response = repairEmployeeService.save(repairId, postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{repairId}/pieces")
    public ResponseEntity<RepairPiecePostResponse> addPiece(@PathVariable Long repairId, @Valid @RequestBody RepairPiecePostRequest postRequest) {
        RepairPiecePostResponse response = repairPieceService.save(repairId, postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{repairId}/employees/{employeeId}")
    public ResponseEntity<Void> removesRepairEmployeeById(@PathVariable Long repairId, @PathVariable Long employeeId) {
        repairEmployeeService.deleteByRepairIdAndEmployeeId(repairId, employeeId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{repairId}/pieces/{pieceId}")
    public ResponseEntity<Void> removesRepairPieceById(@PathVariable Long repairId, @PathVariable Long pieceId) {
        repairPieceService.deleteByRepairIdAndPieceId(repairId, pieceId);

        return ResponseEntity.noContent().build();
    }
}
