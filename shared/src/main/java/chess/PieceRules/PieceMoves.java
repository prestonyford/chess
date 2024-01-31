package chess.PieceRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public abstract class PieceMoves {
    protected ChessBoard board;
    protected ChessPosition pos;
    protected ChessGame.TeamColor color;
    public PieceMoves(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {
        this.board = board;
        this.pos = pos;
        this.color = color;
    }

    protected HashSet<ChessMove> findMovesInDirection(int[] direction) {
        int row = pos.getRow() + direction[0];
        int col = pos.getColumn() + direction[1];
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        while (ChessBoard.validTile(new ChessPosition(row, col))) {
            ChessPosition endPosition = new ChessPosition(row, col);
            // If spot taken by ally, do not add and stop adding that direction to moves
            if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == color) {
                break;
            }
            moves.add(new ChessMove(pos, endPosition, null));
            // If spot is an enemy, capture and stop adding that direction to moves
            if (board.getPiece(endPosition) != null) {
                break;
            }
            row += direction[0];
            col += direction[1];
        }
        return moves;
    }

    public abstract Collection<ChessMove> getMoves();
}
