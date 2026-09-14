package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {
    private final ChessBoard board;
    private ChessPosition startPos;
    private final int startRow;
    private final int startCol;
    private Collection<ChessMove> possibleMoves;

    public BishopMovesCalculator(ChessBoard board, ChessPosition startPos) {
        this.board = board;
        this.startRow = startPos.getRow();
        this.startCol = startPos.getColumn();
        this.possibleMoves = new ArrayList<>();
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int startRow = position.getRow();
        int startCol = position.getColumn();
        diagonal_moves(board, startRow, startCol, 1, -1);
        diagonal_moves(board, startRow, startCol, 1, 1);
        diagonal_moves(board, startRow, startCol, -1, -1);
        diagonal_moves(board, startRow, startCol, -1, 1);
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

    public void diagonal_moves(ChessBoard board, int start_row, int start_col, int rowChange, int colChange) {
        ChessPosition startPosition = new ChessPosition(start_row, start_col);
        ChessPosition currPos = new ChessPosition(start_row, start_col);
        ChessPiece currPiece = board.getPiece(currPos);
        ChessGame.TeamColor currColor = currPiece.getTeamColor();
        ChessPosition nextPos = new ChessPosition(start_row + rowChange, start_col + colChange);

        while (inBounds(nextPos)) {
            currPos = nextPos;
            nextPos = new ChessPosition(currPos.getRow() + rowChange, currPos.getColumn() + colChange);
            currPiece = board.getPiece(currPos);
            if (currPiece == null) {
                possibleMoves.add(new ChessMove(startPosition, currPos, null));
            }
            else {
                if (currPiece.getTeamColor() != currColor) {
                    possibleMoves.add(new ChessMove(startPosition, currPos, null));
                    break;
                }
                else {
                    break;
                }
            }

        }
    }


}
