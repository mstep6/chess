package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition startPos;
    private Collection<ChessMove> possibleMoves;

    public KnightMovesCalculator(ChessBoard board, ChessPosition startPos) {
        this.board = board;
        this.startPos = startPos;
        this.possibleMoves = new ArrayList<>();
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int startRow = position.getRow();
        int startCol = position.getColumn();
        LMove(board, startRow, startCol, 2, 1); //up 2 R 1
        LMove(board, startRow, startCol, 2, -1); //up 2 L 1
        LMove(board, startRow, startCol, -2, 1); //down 2 R 1
        LMove(board, startRow, startCol, -2, -1); //down 2 L 1
        LMove(board, startRow, startCol, 1, 2); // up 1 R 2
        LMove(board, startRow, startCol, 1, -2); // up 1 L 2
        LMove(board, startRow, startCol, -1, 2); // down 1 R 2
        LMove(board, startRow, startCol, -1, -2); // down 1 L 2
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

    public void LMove(ChessBoard board, int startRow, int startCol, int rowChange, int colChange) {
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
