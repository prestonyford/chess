package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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
//        chessBoard = new ChessBoard();
//        chessBoard.resetBoard();
//        teamTurn = TeamColor.WHITE;
    }

    public ChessGame(ChessGame other) {
        chessBoard = new ChessBoard(other.chessBoard);
        teamTurn = other.teamTurn;
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
        Collection<ChessMove> moves = piece.pieceMoves(this.chessBoard, startPosition);
        HashSet<ChessMove> legalMoves = new HashSet<>();

        for (ChessMove move: moves) {
            ChessPiece startPositionPiece = chessBoard.getPiece(move.getStartPosition());
            ChessPiece endPositionPiece = chessBoard.getPiece(move.getEndPosition());

            if (Objects.equals(move.getEndPosition(), new ChessPosition(8, 4))) {
                System.out.println("8,4 found");
            }

            // Make the move but don't promote
            chessBoard.addPiece(startPosition, null);
            chessBoard.addPiece(move.getEndPosition(), piece);

            // If still not in check, add to legalMoves
            if (!isInCheck(piece.getTeamColor())) {
                legalMoves.add(move);
            }

            // Revert the move
            chessBoard.addPiece(startPosition, startPositionPiece);
            chessBoard.addPiece(move.getEndPosition(), endPositionPiece);
        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = chessBoard.getPiece(startPosition);
        Collection<ChessMove> moves = validMoves(startPosition);

        if (
                moves != null && moves.contains(move) &&
                piece.getTeamColor() == teamTurn
        ) {
            chessBoard.addPiece(startPosition, null);
            chessBoard.addPiece(endPosition, piece);

            // Check for promotion and promote accordingly
            if (move.getPromotionPiece() != null) {
                chessBoard.addPiece(endPosition, new ChessPiece(this.teamTurn, move.getPromotionPiece()));
            }

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
                ChessPiece piece = chessBoard.getPiece(startPosition);
                if (piece == null) {
                    continue;
                }
                Collection<ChessMove> pieceMoves = piece.pieceMoves(chessBoard, startPosition);
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
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Determine King's position
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPosition = position;
                }
            }
        }

        Collection<ChessMove> kingMoves = validMoves(kingPosition);
        if (kingMoves == null) {
            return false; // idk man
        }

        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition piecePosition = new ChessPosition(row, col);
                Collection<ChessMove> pieceMoves = validMoves(piecePosition);
                if (pieceMoves == null) {
                    continue;
                }
                kingMoves.removeAll(pieceMoves);
            }
        }

        return kingMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamTurn != teamColor) {
            return false;
        }

        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> pieceMoves = validMoves(position);
                if (pieceMoves != null && !pieceMoves.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
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
