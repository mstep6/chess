package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition startPos;
    private Collection<ChessMove> possibleMoves;

    public PawnMovesCalculator(ChessBoard board, ChessPosition startPos) {
        this.board = board;
        this.startPos = startPos;
        this.possibleMoves = new ArrayList<>();
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece startPiece = board.getPiece(position);
        ChessGame.TeamColor startColor = startPiece.getTeamColor();
        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();

        if (startColor == ChessGame.TeamColor.WHITE) {
            if (startRow == 2) {
                if (board.getPiece(new ChessPosition(startRow + 1, startCol)) == null) {
                    moveOneSquare(board, startPos, 2, 0);
                }
            }
            moveOneSquare(board, startPos, 1, 0);
            captureDiagonal(board, startPos, 1, 1);
            captureDiagonal(board, startPos, 1, -1);
        }
        else if (startColor == ChessGame.TeamColor.BLACK) {
            if (startRow == 7) {
                if (board.getPiece(new ChessPosition(startRow - 1, startCol)) == null) {
                    moveOneSquare(board, startPos, -2, 0);
                }
            }
            moveOneSquare(board, startPos, -1, 0);
            captureDiagonal(board, startPos, -1, 1);
            captureDiagonal(board, startPos, -1, -1);
        }
        return possibleMoves;
    }

    boolean canPromote(ChessBoard board, ChessPosition startPos) {
        ChessPiece currPiece = board.getPiece(startPos);
        ChessGame.TeamColor startColor = currPiece.getTeamColor();
        int startRow = startPos.getRow();
        if (startColor == ChessGame.TeamColor.WHITE && startRow == 7) {
            return true;
        }
        else if (startColor == ChessGame.TeamColor.BLACK && startRow == 2) {
            return true;
        }
        return false;
    }

    void captureDiagonal(ChessBoard board, ChessPosition startPos, int rowChange, int colChange) {
        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();
        ChessPiece currPiece = board.getPiece(startPos);
        ChessPosition nextPos = new ChessPosition(startRow + rowChange, startCol + colChange);
        ChessGame.TeamColor startColor = currPiece.getTeamColor();

        if (PositionChecks.inBounds(nextPos)) {
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (canPromote(board, startPos)) {
                if (nextPiece != null && nextPiece.getTeamColor() != startColor) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.KNIGHT));
                }
            }
            else {
                if (nextPiece != null && nextPiece.getTeamColor() != startColor) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, null));
                }
            }
        }
    }

    void moveOneSquare(ChessBoard board, ChessPosition startPos, int rowChange, int colChange) {
        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();
        ChessPosition nextPos = new ChessPosition(startRow + rowChange, startCol + colChange);

        if (PositionChecks.inBounds(nextPos)) {
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (canPromote(board, startPos)) {
                if (nextPiece == null) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.KNIGHT));
                }
            }
            else {
                if (nextPiece == null) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, null));
                }
            }
        }
    }
}



