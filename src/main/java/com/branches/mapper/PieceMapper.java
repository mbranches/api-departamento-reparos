package com.branches.mapper;

import com.branches.model.Piece;
import com.branches.request.PiecePostRequest;
import com.branches.response.PieceGetResponse;
import com.branches.response.PiecePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PieceMapper {
    @Mapping(target = "id", ignore = true)
    Piece toPiece(PiecePostRequest postRequest);

    List<PieceGetResponse> toPieceGetResponseList(List<Piece> response);

    PieceGetResponse toPieceGetResponse(Piece foundPiece);

    PiecePostResponse toPiecePostResponse(Piece piece);
}
