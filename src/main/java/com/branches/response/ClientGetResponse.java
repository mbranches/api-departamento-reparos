package com.branches.response;

import com.branches.model.Person;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClientGetResponse {
    private Long id;
    private Person person;
    private String email;
}
