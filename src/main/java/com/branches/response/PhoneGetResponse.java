package com.branches.response;

import com.branches.model.PhoneType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneGetResponse {
    private String number;
    private PhoneType phoneType;
}
