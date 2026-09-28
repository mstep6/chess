package chess;

public class PositionChecks {

    public static boolean inBounds(ChessPosition startPos) {
        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();
        if (startRow > 8 || startCol > 8 || startRow < 1 || startCol < 1) {
            return false;
        }
        return true;
    }
}

