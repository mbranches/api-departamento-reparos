package com.branches.mapper;

import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.model.RepairPiece;
import com.branches.response.RepairPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RepairMapper {
    @Mapping(source = "repairEmployees", target = "employees")
    @Mapping(source = "repairPieces", target = "pieces")
    RepairPostResponse toRepairPostResponse(Repair repair, List<RepairPiece> repairPieces, List<RepairEmployee> repairEmployees);
}
