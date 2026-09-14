package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition startPos;
    private Collection<ChessMove> possibleMoves;

    public RookMovesCalculator(ChessBoard board, ChessPosition startPos) {
        this.board = board;
        this.startPos = startPos;
        this.possibleMoves = new ArrayList<>();
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int startRow = position.getRow();
        int startCol = position.getColumn();
        horizontalVerticalMoves(board, startRow, startCol, 1, 0);
        horizontalVerticalMoves(board, startRow, startCol, -1, 0);
        horizontalVerticalMoves(board, startRow, startCol, 0, -1);
        horizontalVerticalMoves(board, startRow, startCol, 0, 1);
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

    public void horizontalVerticalMoves(ChessBoard board, int startRow, int startCol, int rowChange, int colChange) {
        ChessPosition currPos = startPos;
        ChessPiece currPiece = board.getPiece(currPos);
        ChessGame.TeamColor startColor = currPiece.getTeamColor();
        ChessPosition nextPos = new ChessPosition(startRow + rowChange, startCol + colChange);

        while (inBounds(nextPos)) {
            currPos = nextPos;
            nextPos = new ChessPosition(currPos.getRow() + rowChange, currPos.getColumn() + colChange);
            currPiece = board.getPiece(currPos);
            if (currPiece == null) {
                possibleMoves.add(new ChessMove(startPos, currPos, null));
            }
            else {
                if (currPiece.getTeamColor() != startColor) {
                    possibleMoves.add(new ChessMove(startPos, currPos, null));
                    break;
                }
                else {
                    break;
                }
            }
        }
    }
}


