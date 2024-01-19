package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard chessBoard;
    private TeamColor teamTurn;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = chessBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        return piece.pieceMoves(this.chessBoard, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = chessBoard.getPiece(startPosition);
        Collection<ChessMove> moves = validMoves(startPosition);

        if (
                moves != null && moves.contains(move) &&
                piece.getTeamColor() == this.teamTurn
        ) {
            // Make the move
            ChessPosition endPosition = move.getEndPosition();
            chessBoard.addPiece(startPosition, null);
            chessBoard.addPiece(endPosition, piece);

            // Swap team color
            if (this.teamTurn == TeamColor.WHITE) {
                this.teamTurn = TeamColor.BLACK;
            }
            else {
                this.teamTurn = TeamColor.WHITE;
            }
        }
        else {
            // System.out.println("invalid move made!");
            throw new InvalidMoveException();
        }
    }

//    private void checkIfPieceChecks(ChessPosition position) {
//        Collection<ChessMove> newMoves = validMoves(position);
//        if (newMoves == null) {
//            return;
//        }
//        for (ChessMove newMove: newMoves) {
//            ChessPiece targetPiece = chessBoard.getPiece(newMove.getEndPosition());
//            if (
//                    targetPiece != null &&
//                    targetPiece.getPieceType() == ChessPiece.PieceType.KING &&
//                    targetPiece.getTeamColor() != this.teamTurn
//            ) {
//                if (this.teamTurn == TeamColor.WHITE) {
//                    blackKingInCheck = true;
//                }
//                else {
//                    whiteKingInCheck = true;
//                }
//            }
//        }
//    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition startPosition = new ChessPosition(row, col);
                Collection<ChessMove> pieceMoves = validMoves(startPosition);
                if (pieceMoves == null) {
                    continue;
                }
                TeamColor pieceColor = chessBoard.getPiece(startPosition).getTeamColor();

                for (ChessMove move: pieceMoves) {
                    ChessPosition endPosition = move.getEndPosition();
                    ChessPiece targetPiece = chessBoard.getPiece(endPosition);
                    if (
                            targetPiece != null &&
                            targetPiece.getPieceType() == ChessPiece.PieceType.KING &&
                            targetPiece.getTeamColor() != pieceColor &&
                            targetPiece.getTeamColor() == teamColor
                    ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.chessBoard;
    }
}
