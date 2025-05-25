package com.branches.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@With
public class RepairPostRequest {
    @NotNull(message = "The field 'clientId' cannot be null")
    private Long clientId;
    @NotNull(message = "The field 'vehicleId' cannot be null")
    private Long vehicleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate endDate;
}
