package com.branches.request;

import com.branches.model.Address;
import com.branches.model.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientPostRequest {
    @NotBlank(message = "The field 'name' is required")
    private String name;
    @NotBlank(message = "The field 'lastName' is required")
    private String lastName;
    @NotBlank(message = "The field 'email' is required")
    @Email(regexp = "^(?!.*\\.\\.)([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})$", message = "Email is not valid")
    private String email;
    private Address address;
    private List<Phone> phones;
}
