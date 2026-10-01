package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamColor;
    private ChessBoard board;

    public ChessGame() {
        this.teamColor = TeamColor.WHITE;
        this.board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void changeTurns() {
        TeamColor currColor = getTeamTurn();
        if (currColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
        else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(startPosition);

        if (currPiece == null) {
            return null;
        }
        else {
            Collection<ChessMove> possibleMoves = currPiece.pieceMoves(board, startPosition);
            TeamColor currColor = currPiece.getTeamColor();

            for (var move : possibleMoves) {
                ChessPosition startPos = move.getStartPosition();
                ChessPosition endPos = move.getEndPosition();
                ChessBoard newBoard = new ChessBoard(board);
                newBoard.addPiece(endPos, currPiece);
                newBoard.setSquareNull(startPos);
                if (!canCaptureKing(newBoard, currColor)) {
                    validMoves.add(move);
                }
            }
        }
       return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece currPiece = board.getPiece(startPos);
        if (currPiece == null) {
            throw new InvalidMoveException("No piece at start position");
        }
        else {
            Collection<ChessMove> validMoves = validMoves(startPos);
            if (!validMoves.contains(move)) {
                throw new InvalidMoveException("Tried to make an invalid move");
            }
            else {
                board.addPiece(endPos, currPiece);
                board.setSquareNull(startPos);
            }
            changeTurns();
        }
    }

    public boolean canTakePiece(Collection<ChessMove> possibleMoves, ChessPosition piecePosition) {
        for (var move : possibleMoves) {
            ChessPosition endMove = move.getEndPosition();
            if (endMove.equals(piecePosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean canCaptureKing(ChessBoard board, TeamColor teamColor) {
        ChessPosition king = findKing(teamColor);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, new ChessPosition(i, j));
                    if (canTakePiece(possibleMoves, king)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheck(TeamColor teamColor) {
        return canCaptureKing(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        //if it's their turn, king is not in check, but valid moves list is empty, you would be in stalemate
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ChessPosition findKing(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return new ChessPosition(i, j);
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamColor == chessGame.teamColor && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, board);
    }
}


