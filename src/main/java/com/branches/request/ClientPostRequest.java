package com.branches.request;

import com.branches.model.Address;
import com.branches.model.Phone;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientPostRequest {
    private String name;
    private String lastName;
    private Address address;
    private List<Phone> phones;
}
