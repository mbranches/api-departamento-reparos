package com.branches.utils;

import com.branches.model.Piece;
import com.branches.model.RepairPiece;
import com.branches.request.RepairPieceByRepairPostRequest;
import com.branches.response.RepairPieceByRepairResponse;

public class RepairPieceUtils {
    public static RepairPieceByRepairPostRequest newRepairPiecePostRequest() {
        return RepairPieceByRepairPostRequest.builder().pieceId(4L).quantity(5).build();
    }

    public static RepairPiece newRepairPiece() {
        Piece piece = PieceUtils.newPieceToSave();

        return RepairPiece.builder().piece(piece).quantity(5).totalValue(piece.getUnitValue() * 5).build();
    }

    public static RepairPieceByRepairResponse newRepairPieceByRepairPostResponse() {
        Piece piece = PieceUtils.newPieceToSave();

        return RepairPieceByRepairResponse.builder().piece(piece).quantity(5).totalValue(piece.getUnitValue() * 5).build();
    }
}
