package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.mapper.PieceMapper;
import com.branches.model.Piece;
import com.branches.repository.PieceRepository;
import com.branches.request.PiecePostRequest;
import com.branches.request.PiecePostStockRequest;
import com.branches.response.PieceGetResponse;
import com.branches.response.PiecePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PieceService {
    private final PieceRepository repository;
    private final PieceMapper mapper;

    public List<PieceGetResponse> findAll(String name) {
        List<Piece> response = name == null ? repository.findAll() : repository.findAllByNameContaining(name);

        return mapper.toPieceGetResponseList(response);
    }

    public PieceGetResponse findById(Long id) {
        Piece foundPiece = findByIdOrThrowsNotFoundException(id);

        return mapper.toPieceGetResponse(foundPiece);
    }

    public Piece findByIdOrThrowsNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Piece with id '%s' not Found".formatted(id)));
    }

    public PiecePostResponse save(PiecePostRequest postRequest) {
        Piece pieceToSave = mapper.toPiece(postRequest);

        Piece response = repository.save(pieceToSave);

        return mapper.toPiecePostResponse(response);
    }

    public PiecePostResponse addStock(Long pieceId, PiecePostStockRequest postStockRequest) {
        Piece piece = findByIdOrThrowsNotFoundException(pieceId);

        int newStock = piece.getStock() + postStockRequest.getQuantity();
        piece.setStock(newStock);

        Piece response = repository.save(piece);

        return mapper.toPiecePostResponse(response);
    }

    public Piece removesStock(Piece piece, int quantity) {
        int stock = piece.getStock();

        if (stock < quantity) throw new BadRequestException("'" + piece.getName() + "' has insufficient stock." +
                " Available: " + stock + ", Requested: " + quantity);

        piece.setStock(stock - quantity);

        return repository.save(piece);
    }

    public void deleteById(Long id) {
        repository.delete(findByIdOrThrowsNotFoundException(id));
    }
}
