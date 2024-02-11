package chess.pieceRules;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoves extends PieceMoves {
    public PawnMoves(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {
        super(board, pos, color);
    }

    private HashSet<ChessMove> addPromotions(ChessMove move) {
        ChessPosition endPosition = move.getEndPosition();
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        if ((color == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8) || (color == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1)) {
            for (var piece: new ChessPiece.PieceType[]{
                    ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.QUEEN}) {
                moves.add(new ChessMove(move.getStartPosition(), endPosition, piece));
            }
        }
        else {
            moves.add(move);
        }
        return moves;
    }

    @Override
    public Collection<ChessMove> getMoves() {
        HashSet<ChessMove> moves = new HashSet<>();
        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;

        // Forward movement
        ChessPosition forwardPosition = new ChessPosition(pos.getRow() + direction, pos.getColumn());
        if (ChessBoard.validTile(forwardPosition) && board.getPiece(forwardPosition) == null) {
            ChessMove move = new ChessMove(pos, forwardPosition, null);
            moves.addAll(addPromotions(move));
        }

        // Diagonal captures
        for (ChessPosition diagonalPosition: new ChessPosition[] {
                new ChessPosition(pos.getRow() + direction, pos.getColumn() - 1),
                new ChessPosition(pos.getRow() + direction, pos.getColumn() + 1)
        }) {
            if (
                    ChessBoard.validTile(diagonalPosition) &&
                    board.getPiece(diagonalPosition) != null &&
                    board.getPiece(diagonalPosition).getTeamColor() != this.color
            ) {
                ChessMove move = new ChessMove(pos, diagonalPosition, null);
                moves.addAll(addPromotions(move));
            }
        }

        // If first move, Pawn can optionally move two squares
        ChessPosition forwardTwoPosition = new ChessPosition(pos.getRow() + (2 * direction), pos.getColumn());
        if (
                (color == ChessGame.TeamColor.WHITE && pos.getRow() == 2) ||
                (color == ChessGame.TeamColor.BLACK && pos.getRow() == 7)
        ) {
            if (board.getPiece(forwardPosition) == null && board.getPiece(forwardTwoPosition) == null) {
                moves.add(new ChessMove(pos, forwardTwoPosition, null));
            }
        }
        return moves;
    }
}
