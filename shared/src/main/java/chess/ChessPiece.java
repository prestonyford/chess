package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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

        // TODO: Determine promotion piece; currently hardcoded as PAWN
        PieceType promotionPiece = PieceType.PAWN;
        switch (this.type) {
            case KING:
                // TODO: A player is not allowed to make any move that would allow the opponent to capture their King. If your King is in danger of being captured on your turn, you must make a move that removes your King from immediate danger.
                for (var move: new int[][]{
                    {myPosition.getRow() + 1, myPosition.getColumn() - 1},
                    {myPosition.getRow() + 1, myPosition.getColumn()},
                    {myPosition.getRow() + 1, myPosition.getColumn() + 1},
                    {myPosition.getRow(), myPosition.getColumn() - 1},
                    {myPosition.getRow(), myPosition.getColumn()},
                    {myPosition.getRow(), myPosition.getColumn() + 1},
                    {myPosition.getRow() - 1, myPosition.getColumn() - 1},
                    {myPosition.getRow() - 1, myPosition.getColumn()},
                    {myPosition.getRow() - 1, myPosition.getColumn() + 1},
                }) {
                    if (ChessBoard.validTile(move[0], move[1])) {
                        ChessPosition endPosition = new ChessPosition(move[0], move[1]);
                        moves.add(new ChessMove(myPosition, endPosition, promotionPiece));
                    }
                }
                break;
            case QUEEN:
                break;
            case BISHOP:
                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                break;
        }

        return moves;
        // throw new RuntimeException("Not implemented");
    }
}