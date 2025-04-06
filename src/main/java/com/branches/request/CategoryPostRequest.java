package com.branches.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryPostRequest {
    @NotBlank(message = "The field name is required")
    private String name;
    @NotBlank(message = "The field hourlyPrice is required")
    private double hourlyPrice;
}
