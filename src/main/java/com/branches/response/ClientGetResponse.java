package com.branches.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ClientGetResponse {
    private String name;
    private String lastName;
    private AddressGetResponse address;
    private List<PhoneGetResponse> phones;
}
