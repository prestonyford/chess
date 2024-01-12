package chess;

import java.util.ArrayList;
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

        switch (this.type) {
            case KING:
                // TODO: A player is not allowed to make any move that would allow the opponent to capture their King. If your King is in danger of being captured on your turn, you must make a move that removes your King from immediate danger.
                for (var move: new int[][]{
                        {myPosition.getRow() + 1, myPosition.getColumn() - 1},
                        {myPosition.getRow() + 1, myPosition.getColumn()},
                        {myPosition.getRow() + 1, myPosition.getColumn() + 1},
                        {myPosition.getRow(), myPosition.getColumn() - 1},
                        {myPosition.getRow(), myPosition.getColumn() + 1},
                        {myPosition.getRow() - 1, myPosition.getColumn() - 1},
                        {myPosition.getRow() - 1, myPosition.getColumn()},
                        {myPosition.getRow() - 1, myPosition.getColumn() + 1}
                }) {
                    ChessPosition endPosition = new ChessPosition(move[0], move[1]);
                    if (ChessBoard.validTile(endPosition)) {
                        // Cannot move to ally position
                        if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == this.pieceColor) {
                            continue;
                        }
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
                break;
            case QUEEN:
                break;
            case BISHOP:
                for (var direction: new int[][]{
                        {1, -1},
                        {1, 1},
                        {-1, -1},
                        {-1, 1}
                }) {
                    int row = myPosition.getRow() + direction[0];
                    int col = myPosition.getColumn() + direction[1];
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
                }
                break;
            case KNIGHT:
                for (var move: new int[][]{
                        {myPosition.getRow() + 2, myPosition.getColumn() - 1},
                        {myPosition.getRow() + 2, myPosition.getColumn() + 1},
                        {myPosition.getRow() + 1, myPosition.getColumn() - 2},
                        {myPosition.getRow() + 1, myPosition.getColumn() + 2},
                        {myPosition.getRow() - 1, myPosition.getColumn() - 2},
                        {myPosition.getRow() - 1, myPosition.getColumn() + 2},
                        {myPosition.getRow() - 2, myPosition.getColumn() - 1},
                        {myPosition.getRow() - 2, myPosition.getColumn() + 1}
                }) {
                    ChessPosition endPosition = new ChessPosition(move[0], move[1]);
                    if (ChessBoard.validTile(endPosition)) {
                        // Cannot move to ally position
                        if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == this.pieceColor) {
                            continue;
                        }
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
                break;
            case ROOK:
                break;
            case PAWN:
                if (this.pieceColor == ChessGame.TeamColor.WHITE) {
                    // Forward movement
                    ChessPosition forwardPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                    if (ChessBoard.validTile(forwardPosition) && board.getPiece(forwardPosition) == null) {
                        ChessMove move = new ChessMove(myPosition, forwardPosition, null);
                        moves.addAll(pawnPromotion(move, this.pieceColor));
                    }
                    // Diagonal capture left
                    ChessPosition diagonalPositionLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                    if (ChessBoard.validTile(diagonalPositionLeft) && board.getPiece(diagonalPositionLeft) != null && board.getPiece(diagonalPositionLeft).getTeamColor() != this.pieceColor) {
                        ChessMove move = new ChessMove(myPosition, diagonalPositionLeft, null);
                        moves.addAll(pawnPromotion(move, this.pieceColor));
                    }
                    // Diagonal capture right
                    ChessPosition diagonalPositionRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                    if (ChessBoard.validTile(diagonalPositionRight) && board.getPiece(diagonalPositionRight) != null && board.getPiece(diagonalPositionRight).getTeamColor() != this.pieceColor) {
                        ChessMove move = new ChessMove(myPosition, diagonalPositionRight, null);
                        moves.addAll(pawnPromotion(move, this.pieceColor));
                    }
                    // If first move, Pawn can optionally move two squares
                    ChessPosition interPosition = new ChessPosition(3, myPosition.getColumn());
                    ChessPosition endPosition = new ChessPosition(4, myPosition.getColumn());
                    if (myPosition.getRow() == 2 && board.getPiece(endPosition) == null) {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
                else if (this.pieceColor == ChessGame.TeamColor.BLACK) {
                    // Forward movement
                    ChessPosition forwardPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                    if (ChessBoard.validTile(forwardPosition) && board.getPiece(forwardPosition) == null) {
                        ChessMove move = new ChessMove(myPosition, forwardPosition, null);
                        moves.addAll(pawnPromotion(move, this.pieceColor));
                    }
                    // Diagonal capture left
                    ChessPosition diagonalPositionLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                    if (ChessBoard.validTile(diagonalPositionLeft) && board.getPiece(diagonalPositionLeft) != null && board.getPiece(diagonalPositionLeft).getTeamColor() != this.pieceColor) {
                        ChessMove move = new ChessMove(myPosition, diagonalPositionLeft, null);
                        moves.addAll(pawnPromotion(move, this.pieceColor));
                    }
                    // Diagonal capture right
                    ChessPosition diagonalPositionRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                    if (ChessBoard.validTile(diagonalPositionRight) && board.getPiece(diagonalPositionRight) != null && board.getPiece(diagonalPositionRight).getTeamColor() != this.pieceColor) {
                        ChessMove move = new ChessMove(myPosition, diagonalPositionRight, null);
                        moves.addAll(pawnPromotion(move, this.pieceColor));
                    }
                    // If first move, Pawn can optionally move two squares
                    ChessPosition interPosition = new ChessPosition(6, myPosition.getColumn());
                    ChessPosition endPosition = new ChessPosition(5, myPosition.getColumn());
                    if (myPosition.getRow() == 7 && board.getPiece(interPosition) == null && board.getPiece(endPosition) == null) {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }


                break;
        }

        return moves;
        // throw new RuntimeException("Not implemented");
    }

    private static HashSet<ChessMove> pawnPromotion(ChessMove move, ChessGame.TeamColor color) {
        ChessPosition endPosition = move.getEndPosition();
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        if ((color == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8) || (color == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1)) {
            for (var piece: new ChessPiece.PieceType[]{
                    PieceType.ROOK,
                    PieceType.KNIGHT,
                    PieceType.BISHOP,
                    PieceType.QUEEN}) {
                moves.add(new ChessMove(move.getStartPosition(), endPosition, piece));
            }
        }
        else {
            moves.add(move);
        }
        return moves;
    }

}
