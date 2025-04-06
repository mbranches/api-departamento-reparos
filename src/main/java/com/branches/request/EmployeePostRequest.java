package com.branches.request;

import com.branches.model.Address;
import com.branches.model.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployeePostRequest {
    @NotBlank(message = "The field name is required")
    private String name;
    @NotNull(message = "The field lastName is required")
    private String lastName;
    @NotNull(message = "The field categoryId is required")
    private Long categoryId;
    private Address address;
    private List<Phone> phones;
}
