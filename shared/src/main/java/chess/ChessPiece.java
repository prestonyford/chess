package chess;

import chess.PieceRules.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece other) {
        this.pieceColor = other.pieceColor;
        this.type = other.type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        // throw new RuntimeException("Not implemented");
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        // throw new RuntimeException("Not implemented");
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        return switch (this.type) {
            case KING -> new KingMoves(board, myPosition, pieceColor).getMoves();
            case QUEEN -> new QueenMoves(board, myPosition, pieceColor).getMoves();
            case BISHOP -> new BishopMoves(board, myPosition, pieceColor).getMoves();
            case KNIGHT -> new KnightMoves(board, myPosition, pieceColor).getMoves();
            case ROOK -> new RookMoves(board, myPosition, pieceColor).getMoves();
            case PAWN -> new PawnMoves(board, myPosition, pieceColor).getMoves();
        };
    }

    private HashSet<ChessMove> findMovesInDirection(ChessBoard board, ChessPosition myPosition, int[] direction) {
        int row = myPosition.getRow() + direction[0];
        int col = myPosition.getColumn() + direction[1];
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        while (ChessBoard.validTile(new ChessPosition(row, col))) {
            ChessPosition endPosition = new ChessPosition(row, col);
            // If spot taken by ally, do not add and stop adding that direction to moves
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == this.pieceColor) {
                break;
            }
            moves.add(new ChessMove(myPosition, endPosition, null));
            // If spot is an enemy, capture and stop adding that direction to moves
            if (board.getPiece(endPosition) != null) {
                break;
            }
            row += direction[0];
            col += direction[1];
        }
        return moves;
    }

    @Override
    public String toString() {
        return "CP{" +
                pieceColor +
                " " + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
