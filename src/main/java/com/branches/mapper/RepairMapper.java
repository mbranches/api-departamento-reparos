package com.branches.mapper;

import com.branches.model.Repair;
import com.branches.response.RepairGetResponse;
import com.branches.response.RepairPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RepairMapper {
    RepairPostResponse toRepairPostResponse(Repair repair);

    RepairGetResponse toRepairGetResponse(Repair repair);

    List<RepairGetResponse> toRepairGetResponseList(List<Repair> repairList);
}
