package chess.pieceRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class RookMoves extends PieceMoves {
    public RookMoves(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {
        super(board, pos, color);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        HashSet<ChessMove> moves = new HashSet<>();
        for (var direction: new int[][]{
                {1, 0},
                {0, -1},
                {0, 1},
                {-1, 0},
        }) {
            moves.addAll(findMovesInDirection(direction));
        }
        return moves;
    }
}
