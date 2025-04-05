package com.branches.utils;

import com.branches.model.Piece;
import com.branches.request.PiecePostRequest;
import com.branches.response.PieceGetResponse;
import com.branches.response.PiecePostResponse;

import java.util.ArrayList;
import java.util.List;

public class PieceUtils {
    public static List<Piece> newPieceList() {
        Piece piece1 = Piece.builder().id(1L).name("Óleo de motor").unitValue(50).build();
        Piece piece2 = Piece.builder().id(2L).name("Filtro de ar").unitValue(80).build();
        Piece piece3 = Piece.builder().id(3L).name("Pneu Aro 15").unitValue(407).build();

        return new ArrayList<>(List.of(piece1, piece2, piece3));
    }

    public static List<PieceGetResponse> newPieceGetResponseList() {
        PieceGetResponse piece1 = PieceGetResponse.builder().name("Óleo de motor").unitValue(50).build();
        PieceGetResponse piece2 = PieceGetResponse.builder().name("Filtro de ar").unitValue(80).build();
        PieceGetResponse piece3 = PieceGetResponse.builder().name("Pneu Aro 15").unitValue(407).build();

        return new ArrayList<>(List.of(piece1, piece2, piece3));
    }

    public static Piece newPieceToSave() {
        return Piece.builder().id(4L).name("Pastilha de freio").unitValue(120).build();
    }

    public static PiecePostRequest newPiecePostRequest() {
        return PiecePostRequest.builder().name("Pastilha de freio").unitValue(120).build();
    }

    public static PiecePostResponse newPiecePostResponse() {
        return PiecePostResponse.builder().id(4L).name("Pastilha de freio").unitValue(120).build();
    }
}
