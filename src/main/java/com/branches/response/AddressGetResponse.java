package com.branches.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressGetResponse {
    private String street;
    private String district;
    private String city;
    private String state;
}
