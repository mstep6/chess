package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{
    private final ChessBoard board;
    private final ChessPosition startPos;
    private Collection<ChessMove> possibleMoves;

    public KingMovesCalculator(ChessBoard board, ChessPosition startPos) {
        this.board = board;
        this.startPos = startPos;
        this.possibleMoves = new ArrayList<>();
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        PossibleMoves.checkOneMove(board, startPos, 1, 0, possibleMoves);
        PossibleMoves.checkOneMove(board, startPos, -1, 0, possibleMoves);
        PossibleMoves.checkOneMove(board, startPos, 0, 1, possibleMoves);
        PossibleMoves.checkOneMove(board, startPos, 0, -1, possibleMoves);
        PossibleMoves.checkOneMove(board, startPos, 1, -1, possibleMoves);
        PossibleMoves.checkOneMove(board, startPos, 1, 1, possibleMoves);
        PossibleMoves.checkOneMove(board, startPos, -1, -1, possibleMoves);
        PossibleMoves.checkOneMove(board, startPos, -1, 1, possibleMoves);

        return possibleMoves;
    }

}


