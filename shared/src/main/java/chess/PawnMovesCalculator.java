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
        int startRow = position.getRow();
        int startCol = position.getColumn();
        ChessPiece startPiece = board.getPiece(position);
        ChessGame.TeamColor startColor = startPiece.getTeamColor();
        if (startRow == 2 && startColor == ChessGame.TeamColor.WHITE) {
            ChessPosition nextPos = new ChessPosition(startRow + 1, startCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                pawnMoveForward(board, startRow, startCol, 2, 0);
            }
        }
        else if (startRow == 7 && startColor == ChessGame.TeamColor.BLACK) {
            ChessPosition nextPos = new ChessPosition(startRow + -1, startCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                pawnMoveForward(board, startRow, startCol, -2, 0);
            }

        }

        if (startColor == ChessGame.TeamColor.WHITE) {
            pawnMoveForward(board, startRow, startCol, 1, 0);
            pawnMoveForward(board, startRow, startCol, 1, 1);
            pawnMoveForward(board, startRow, startCol, 1, -1);
        }
        else {
            pawnMoveForward(board, startRow, startCol, -1, 0);
            pawnMoveForward(board, startRow, startCol, -1, -1);
            pawnMoveForward(board, startRow, startCol, -1, 1);
        }

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

    public void pawnMoveForward(ChessBoard board, int startRow, int startCol, int rowChange, int colChange) {
        ChessPiece currPiece = board.getPiece(startPos);
        ChessGame.TeamColor startColor = currPiece.getTeamColor();
        ChessPosition nextPos = new ChessPosition(startRow + rowChange, startCol + colChange);

        if (inBounds(nextPos)) {
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (startRow == 7 && startColor == ChessGame.TeamColor.WHITE) {
                if ((nextPiece != null) && (nextPiece.getTeamColor() != startColor) && (startCol != nextPos.getColumn())) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.KNIGHT));
                }
                else if (nextPiece == null && startCol == nextPos.getColumn()) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.KNIGHT));
                }
            } else if (startRow == 2 && startColor == ChessGame.TeamColor.BLACK) {
                if ((nextPiece != null) && (nextPiece.getTeamColor() != startColor) && (startCol != nextPos.getColumn())) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.KNIGHT));
                }
                else if (nextPiece == null && startCol == nextPos.getColumn()) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(startPos, nextPos, ChessPiece.PieceType.KNIGHT));
                }
            }
            else {
                if ((nextPiece != null) && (nextPiece.getTeamColor() != startColor) && (startCol != nextPos.getColumn())) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, null));
                } else if (nextPiece == null && startCol == nextPos.getColumn()) {
                    possibleMoves.add(new ChessMove(startPos, nextPos, null));
                }
            }
        }
    }

}

