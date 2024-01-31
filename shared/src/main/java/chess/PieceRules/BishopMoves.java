package chess.PieceRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoves extends PieceMoves {
    public BishopMoves(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {
        super(board, pos, color);
    }

    @Override
    public Collection<ChessMove> getMoves() {
        HashSet<ChessMove> moves = new HashSet<>();
        for (var direction: new int[][]{
                {1, -1},
                {1, 1},
                {-1, -1},
                {-1, 1}
        }) {
            moves.addAll(findMovesInDirection(direction));
        }
        return moves;
    }
}
