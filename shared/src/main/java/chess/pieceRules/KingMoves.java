package chess.pieceRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KingMoves extends PieceMoves {
    public KingMoves(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {
        super(board, pos, color);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        HashSet<ChessMove> moves = new HashSet<>();
        for (var endPosition : new ChessPosition[]{
                new ChessPosition(pos.getRow() + 1, pos.getColumn() - 1),
                new ChessPosition(pos.getRow() + 1, pos.getColumn()),
                new ChessPosition(pos.getRow() + 1, pos.getColumn() + 1),
                new ChessPosition(pos.getRow(), pos.getColumn() - 1),
                new ChessPosition(pos.getRow(), pos.getColumn() + 1),
                new ChessPosition(pos.getRow() - 1, pos.getColumn() - 1),
                new ChessPosition(pos.getRow() - 1, pos.getColumn()),
                new ChessPosition(pos.getRow() - 1, pos.getColumn() + 1)
        }) {
            if (invalidTargetTile(endPosition)) {
                continue;
            }
            moves.add(new ChessMove(pos, endPosition, null));
        }
        return moves;
    }
}
