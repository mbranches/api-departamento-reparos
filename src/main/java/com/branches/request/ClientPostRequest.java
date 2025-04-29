package com.branches.request;

import com.branches.model.Address;
import com.branches.model.Phone;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientPostRequest {
    @NotBlank(message = "The field name is required")
    private String name;
    @NotBlank(message = "The field lastName is required")
    private String lastName;
    private String email;
    private Address address;
    private List<Phone> phones;
}
