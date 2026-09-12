package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {
    private final ChessBoard board;
    private ChessPosition start_pos;
    private final int start_row;
    private final int start_col;
    private Collection<ChessMove> possible_moves;

    public BishopMovesCalculator(ChessBoard board, ChessPosition start_pos) {
        this.board = board;
        this.start_row = start_pos.getRow();
        this.start_col = start_pos.getColumn();
        this.possible_moves = new ArrayList<>();
    }

    public void CalculateBishopMoves(ChessBoard board, int start_row, int start_col) {
        boolean blocked = true;
    }

    public boolean validMove(ChessBoard board, ChessPosition next_position, ChessGame.TeamColor curr_color) {
        ChessPiece next_piece = board.getPiece(next_position);
        ChessGame.TeamColor color = next_piece.getTeamColor();
//        check to make sure you're not off the board
//        check if the next position is empty, if not empty, check if it has a piece of the opposite color

        return false;
    }

    public void left_diagonal_one(ChessBoard board, int start_row, int start_col) {
        ChessPosition curr_pos = new ChessPosition(start_row, start_col);
        ChessPiece curr_piece = board.getPiece(curr_pos);
        ChessGame.TeamColor curr_color = curr_piece.getTeamColor();
        ChessPosition next_pos = new ChessPosition(start_row + 1, start_col - 1);

//        while (validMove(board, next_pos, curr_color ) {
//             possible_moves.add(next_pos);
//             next_pos = new ChessPosition(curr_row + 1, curr_col - 1);
//        }

    }


}
