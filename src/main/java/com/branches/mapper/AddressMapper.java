package com.branches.mapper;

import com.branches.model.Address;
import com.branches.response.AddressGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
    AddressGetResponse toAddressGetResponse(Address address);
}
