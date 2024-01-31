package chess.PieceRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoves extends PieceMoves {
    public KnightMoves(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {
        super(board, pos, color);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        HashSet<ChessMove> moves = new HashSet<>();
        for (var move: new int[][] {
                {pos.getRow() + 2, pos.getColumn() - 1},
                {pos.getRow() + 2, pos.getColumn() + 1},
                {pos.getRow() + 1, pos.getColumn() - 2},
                {pos.getRow() + 1, pos.getColumn() + 2},
                {pos.getRow() - 1, pos.getColumn() - 2},
                {pos.getRow() - 1, pos.getColumn() + 2},
                {pos.getRow() - 2, pos.getColumn() - 1},
                {pos.getRow() - 2, pos.getColumn() + 1}
        }) {
            ChessPosition endPosition = new ChessPosition(move[0], move[1]);
            if (
                    !ChessBoard.validTile(endPosition) ||
                    (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == color)
            ) {
                continue;
            }
            moves.add(new ChessMove(pos, endPosition, null));
        }
        return moves;
    }
}
