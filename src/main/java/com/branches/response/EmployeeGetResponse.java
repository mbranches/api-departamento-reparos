package com.branches.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployeeGetResponse {
    private String name;
    private String lastName;
    private CategoryGetResponse category;
    private AddressGetResponse address;
    private List<PhoneGetResponse> phones;
}
