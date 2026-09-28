package chess;

import java.util.Collection;

public class PossibleMoves {

    public static void checkContinuousMoves(ChessBoard board, ChessPosition startPos,
                                            int rowChange, int colChange, Collection<ChessMove> possibleMoves) {
        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();
        ChessPiece currPiece = board.getPiece(startPos);
        ChessPosition nextPos = new ChessPosition(startRow + rowChange, startCol + colChange);
        ChessGame.TeamColor startColor = currPiece.getTeamColor();

        while (PositionChecks.inBounds(nextPos)) {
            currPiece = board.getPiece(nextPos);
            if (currPiece == null) {
                possibleMoves.add(new ChessMove(startPos, nextPos, null));
            }
            else {
                if (currPiece.getTeamColor() != startColor) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, null));
                    break;
                }
                else {
                    break;
                }

            }
            nextPos = new ChessPosition(nextPos.getRow() + rowChange, nextPos.getColumn() + colChange);
        }
    }

    public static void checkOneMove(ChessBoard board, ChessPosition startPos, int rowChange, int colChange, Collection<ChessMove> possibleMoves) {
        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();
        ChessPiece currPiece = board.getPiece(startPos);
        ChessPosition nextPos = new ChessPosition(startRow + rowChange, startCol + colChange);
        ChessGame.TeamColor startColor = currPiece.getTeamColor();

        if (PositionChecks.inBounds(nextPos)) {
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                possibleMoves.add(new ChessMove(startPos, nextPos, null));
            }
            else if (nextPiece.getTeamColor() != startColor) {
                possibleMoves.add(new ChessMove(startPos, nextPos, null));
            }
        }
    }
}

