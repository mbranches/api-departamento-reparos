package com.branches.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PiecePostRequest {
    @NotBlank(message = "The field name is required")
    private String name;
    @NotBlank(message = "The field unitValue is required")
    private double unitValue;
    private int stock;
}
