package com.branches.request;

import com.branches.model.Address;
import com.branches.model.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.List;

@Data
@Builder
@With
public class EmployeePutRequest {
    @NotNull(message = "The field 'id' is required")
    private Long id;
    @NotBlank(message = "The field 'name' is required")
    private String name;
    @NotBlank(message = "The field 'lastName' is required")
    private String lastName;
    @NotNull(message = "The field 'categoryId' is required")
    private Long categoryId;
    @NotNull(message = "The field 'address' is required")
    private Address address;
    @NotNull(message = "The field 'phones' is required")
    private List<Phone> phones;
}
