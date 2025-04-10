package com.branches.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairPieceByRepairPostRequest {
    private Long pieceId;
    private int quantity;
}
