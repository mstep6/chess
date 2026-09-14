package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition startPos;
    private Collection<ChessMove> possibleMoves;

    public KingMovesCalculator(ChessBoard board, ChessPosition startPos) {
        this.board = board;
        this.startPos = startPos;
        this.possibleMoves = new ArrayList<>();
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int startRow = position.getRow();
        int startCol = position.getColumn();
        moveOneSquare(board, startRow, startCol, 1, 0); //up
        moveOneSquare(board, startRow, startCol, -1, 0); //down
        moveOneSquare(board, startRow, startCol, 0, 1); //right
        moveOneSquare(board, startRow, startCol, 0, -1); //left
        moveOneSquare(board, startRow, startCol, 1, 1); //r top diagonal
        moveOneSquare(board, startRow, startCol, 1, -1); //l top diagonal
        moveOneSquare(board, startRow, startCol, -1, -1); //l bottom diagonal
        moveOneSquare(board, startRow, startCol, -1, 1); //r bottom diagonal
        return possibleMoves;
    }

    public boolean inBounds(ChessPosition position) {
        //Check to make sure you're not off the board
        int row = position.getRow();
        int col = position.getColumn();
        if (row > 8 || row < 1 || col > 8 || col < 1) {
            return false;
        }
        return true;
    }

    public void moveOneSquare(ChessBoard board, int startRow, int startCol, int rowChange, int colChange) {
        ChessPiece startPiece = board.getPiece(startPos);
        ChessGame.TeamColor startColor = startPiece.getTeamColor();
        ChessPosition nextPos = new ChessPosition(startRow + rowChange, startCol + colChange);

        if (inBounds(nextPos)) {
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                possibleMoves.add(new ChessMove(startPos, nextPos, null));
            }
            else {
                if (nextPiece.getTeamColor() != startColor) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, null));
                }
            }
        }
    }
}


