package com.branches.mapper;

import com.branches.model.Client;
import com.branches.response.ClientGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    List<ClientGetResponse> toClientGetResponseList(List<Client> clientList);
}
