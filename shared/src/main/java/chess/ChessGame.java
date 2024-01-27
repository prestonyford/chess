package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard chessBoard;
    private TeamColor teamTurn;

    // Castling
    private boolean whiteKingHasMoved = false;
    private boolean whiteLeftRookHasMoved = false;
    private boolean whiteRightRookHasMoved = false;
    private boolean blackKingHasMoved = false;
    private boolean blackLeftRookHasMoved = false;
    private boolean blackRightRookHasMoved = false;

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
        TeamColor pieceColor = piece.getTeamColor();

        for (ChessMove move: moves) {
            ChessPiece startPositionPiece = chessBoard.getPiece(move.getStartPosition());
            ChessPiece endPositionPiece = chessBoard.getPiece(move.getEndPosition());

            if (!doesMoveCauseCheck(move, pieceColor)) {
                legalMoves.add(move);
            }
        }

        // Castling
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !isInCheck(pieceColor)) {
            int row = (pieceColor == TeamColor.WHITE) ? 1 : 8;
            if (pieceColor == TeamColor.WHITE && !whiteKingHasMoved) {
                if (!whiteLeftRookHasMoved) {
                    // Ensure path is clear
                    if (
                            chessBoard.getPiece(new ChessPosition(row, 4)) == null &&
                            chessBoard.getPiece(new ChessPosition(row, 3)) == null &&
                            chessBoard.getPiece(new ChessPosition(row, 2)) == null
                    ) {
                        ChessMove move = new ChessMove(startPosition, new ChessPosition(row, 3), null);
                        // Move cannot cause the King or Rook to be in danger
                        if (
                                !doesMoveCauseCheck(move, pieceColor) &&
                                !doesMoveCausePositionDanger(move, new ChessPosition(row, 4), pieceColor)
                        ) {
                            legalMoves.add(move);
                        }
                    }
                }
                if (!whiteRightRookHasMoved) {
                    // Ensure path is clear
                    if (
                            chessBoard.getPiece(new ChessPosition(row, 6)) == null &&
                            chessBoard.getPiece(new ChessPosition(row, 7)) == null
                    ) {
                        ChessMove move = new ChessMove(startPosition, new ChessPosition(row, 7), null);
                        if (
                                !doesMoveCauseCheck(move, pieceColor) &&
                                !doesMoveCausePositionDanger(move, new ChessPosition(row, 6), pieceColor)
                        ) {
                            legalMoves.add(move);
                        }
                    }
                }
            }
            else if (pieceColor == TeamColor.BLACK && !blackKingHasMoved) {
                if (!blackLeftRookHasMoved) {
                    // Ensure path is clear
                    if (
                            chessBoard.getPiece(new ChessPosition(row, 4)) == null &&
                            chessBoard.getPiece(new ChessPosition(row, 3)) == null &&
                            chessBoard.getPiece(new ChessPosition(row, 2)) == null
                    ) {
                        ChessMove move = new ChessMove(startPosition, new ChessPosition(row, 3), null);
                        // Move cannot cause the King or Rook to be in danger
                        if (
                                !doesMoveCauseCheck(move, pieceColor) &&
                                !doesMoveCausePositionDanger(move, new ChessPosition(row, 4), pieceColor)
                        ) {
                            legalMoves.add(move);
                        }
                    }
                }
                if (!blackRightRookHasMoved) {
                    // Ensure path is clear
                    if (
                            chessBoard.getPiece(new ChessPosition(row, 6)) == null &&
                            chessBoard.getPiece(new ChessPosition(row, 7)) == null
                    ) {
                        ChessMove move = new ChessMove(startPosition, new ChessPosition(row, 7), null);
                        if (
                                !doesMoveCauseCheck(move, pieceColor) &&
                                !doesMoveCausePositionDanger(move, new ChessPosition(row, 6), pieceColor)
                        ) {
                            legalMoves.add(move);
                        }
                    }
                }
            }
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
                moves != null &&
                moves.contains(move) &&
                piece.getTeamColor() == teamTurn
        ) {
            chessBoard.movePiece(move);

            // Update castling checks or castle if it was a king
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    whiteKingHasMoved = true;
                }
                else {
                    blackKingHasMoved = true;
                }

                // If the move was a king castling, update the rook
                if (endPosition.getColumn() - startPosition.getColumn() == 2) {
                    if (piece.getTeamColor() == TeamColor.WHITE) {
                        whiteRightRookHasMoved = true;
                        ChessPosition rookPos = new ChessPosition(1, 8);
                        chessBoard.movePiece(new ChessMove(rookPos, new ChessPosition(1, 6), null));
                    }
                    else if (piece.getTeamColor() == TeamColor.BLACK) {
                        blackRightRookHasMoved = true;
                        ChessPosition rookPos = new ChessPosition(8, 8);
                        chessBoard.movePiece(new ChessMove(rookPos, new ChessPosition(8, 6), null));
                    }
                }
                else if (endPosition.getColumn() - startPosition.getColumn() == -2) {
                    if (piece.getTeamColor() == TeamColor.WHITE) {
                        whiteLeftRookHasMoved = true;
                        ChessPosition rookPos = new ChessPosition(1, 1);
                        chessBoard.movePiece(new ChessMove(rookPos, new ChessPosition(1, 4), null));
                    }
                    else if (piece.getTeamColor() == TeamColor.BLACK) {
                        blackLeftRookHasMoved = true;
                        ChessPosition rookPos = new ChessPosition(8, 1);
                        chessBoard.movePiece(new ChessMove(rookPos, new ChessPosition(8, 4), null));
                    }
                }
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    if (startPosition.equals(new ChessPosition(1, 1))) {
                        whiteLeftRookHasMoved = true;
                    }
                    else if (startPosition.equals(new ChessPosition(1, 8))) {
                        whiteRightRookHasMoved = true;
                    }
                }
                else {
                    if (startPosition.equals(new ChessPosition(8, 1))) {
                        blackLeftRookHasMoved = true;
                    }
                    else if (startPosition.equals(new ChessPosition(8, 8))) {
                        blackRightRookHasMoved = true;
                    }
                }
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
            System.out.println(moves.contains(move)? "Contained move: " + move.toString() : "Did not contain move "  + move.toString());
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
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return isInDanger(position, teamColor);
                }
            }
        }
        System.out.println("Could not find king to determine if in check");
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
        whiteKingHasMoved = false;
        whiteLeftRookHasMoved = false;
        whiteRightRookHasMoved = false;
        blackKingHasMoved = false;
        blackLeftRookHasMoved = false;
        blackRightRookHasMoved = false;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.chessBoard;
    }

    private boolean isInDanger(ChessPosition pos, TeamColor teamColor) {
        ChessPiece target = chessBoard.getPiece(pos);

        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition startPosition = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(startPosition);

                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }

                Collection<ChessMove> pieceMoves = piece.pieceMoves(chessBoard, startPosition);
                for (ChessMove move: pieceMoves) {
                    ChessPosition endPosition = move.getEndPosition();
                    if (endPosition.equals(pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean doesMoveCauseCheck(ChessMove move, TeamColor teamColor) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece startPositionPiece = chessBoard.getPiece(startPosition);
        ChessPiece endPositionPiece = chessBoard.getPiece(endPosition);

        // Make the move but don't promote
        chessBoard.movePiece(move);

        boolean res = isInCheck(teamColor);

        // Revert the move
        chessBoard.addPiece(startPosition, startPositionPiece);
        chessBoard.addPiece(endPosition, endPositionPiece);

        return res;
    }

    private boolean doesMoveCausePositionDanger(ChessMove move, ChessPosition piecePosition, TeamColor teamColor) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece startPositionPiece = chessBoard.getPiece(startPosition);
        ChessPiece endPositionPiece = chessBoard.getPiece(endPosition);

        chessBoard.movePiece(move);

        boolean res = isInDanger(piecePosition, teamColor);

        // Revert the move
        chessBoard.addPiece(startPosition, startPositionPiece);
        chessBoard.addPiece(endPosition, endPositionPiece);

        return res;
    }
}
