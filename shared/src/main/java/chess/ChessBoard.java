package chess;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    public ChessBoard(ChessBoard other) {
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                board[row][col] = new ChessPiece(other.board[row][col]);
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[8 - position.getRow()][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[8 - position.getRow()][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Fill pawns
        for (int col = 1; col <= 8; ++col) {
            addPiece(new ChessPosition(2,col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7,col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        int col = 1;
        for (var piece: new ChessPiece.PieceType[]{
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        }) {
            addPiece(new ChessPosition(1,col), new ChessPiece(ChessGame.TeamColor.WHITE, piece));
            addPiece(new ChessPosition(8,col), new ChessPiece(ChessGame.TeamColor.BLACK, piece));
            ++col;
        }

        // All other tiles null, just in case
        for (int row = 3; row <= 6; ++row) {
            for (col = 1; col <= 8; ++col) {
                addPiece(new ChessPosition(row, col), null);
            }
        }
    }

    // HELPER
    public static boolean validTile(ChessPosition pos) {
        int row = pos.getRow();
        int col = pos.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (ChessPiece[] row: board) {
            sb.append(Arrays.toString(row));
        }
        return sb.toString();
    }
}
