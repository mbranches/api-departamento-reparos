package com.branches.mapper;

import com.branches.model.Phone;
import com.branches.response.PhoneGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PhoneMapper {
    List<PhoneGetResponse> toPhoneGetResponseList(List<Phone> phoneList);
}
