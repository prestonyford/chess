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

    // Castling
    private boolean whiteKingHasMoved = false;
    private boolean whiteLeftRookHasMoved = false;
    private boolean whiteRightRookHasMoved = false;
    private boolean blackKingHasMoved = false;
    private boolean blackLeftRookHasMoved = false;
    private boolean blackRightRookHasMoved = false;

    // En Passant
    private ChessPosition lastWhitePawnToDoubleMove = null;
    private ChessPosition lastBlackPawnToDoubleMove = null;

    private Collection<ChessMove> validEnPassantMoves(ChessPosition startPosition, ChessPiece piece) {
        HashSet<ChessMove> legalMoves = new HashSet<>();
        // If sideways neighbor is enemy pawn
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessPosition toLeft = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1);
            ChessPosition toRight = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1);

            if (piece.getTeamColor() == TeamColor.WHITE) {
                if (toLeft.equals(lastBlackPawnToDoubleMove) || toRight.equals(lastBlackPawnToDoubleMove)) {
                    ChessPosition endPosition = new ChessPosition(lastBlackPawnToDoubleMove.getRow() + 1, lastBlackPawnToDoubleMove.getColumn());
                    ChessMove move = new ChessMove(startPosition, endPosition, null);
                    legalMoves.add(move);
                }
            } else {
                if (toLeft.equals(lastWhitePawnToDoubleMove) || toRight.equals(lastWhitePawnToDoubleMove)) {
                    ChessPosition endPosition = new ChessPosition(lastWhitePawnToDoubleMove.getRow() - 1, lastWhitePawnToDoubleMove.getColumn());
                    ChessMove move = new ChessMove(startPosition, endPosition, null);
                    legalMoves.add(move);
                }
            }
        }
        return legalMoves;
    }

    private Collection<ChessMove> validCastlingMoves(ChessPosition startPosition, ChessPiece piece, TeamColor pieceColor) {
        HashSet<ChessMove> legalMoves = new HashSet<>();
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !isInCheck(pieceColor)) {
            if (pieceColor == TeamColor.WHITE && !whiteKingHasMoved) {
                if (!whiteLeftRookHasMoved) {
                    // Ensure path is clear
                    if (
                            chessBoard.getPiece(new ChessPosition(1, 4)) == null &&
                                    chessBoard.getPiece(new ChessPosition(1, 3)) == null &&
                                    chessBoard.getPiece(new ChessPosition(1, 2)) == null
                    ) {
                        ChessMove move = new ChessMove(startPosition, new ChessPosition(1, 3), null);
                        // Move cannot cause the King or Rook to be in danger
                        if (
                                !doesMoveCauseCheck(move, pieceColor) &&
                                        !doesMoveCausePositionDanger(move, new ChessPosition(1, 4), pieceColor)
                        ) {
                            legalMoves.add(move);
                        }
                    }
                }
                if (!whiteRightRookHasMoved) {
                    // Ensure path is clear
                    if (
                            chessBoard.getPiece(new ChessPosition(1, 6)) == null &&
                                    chessBoard.getPiece(new ChessPosition(1, 7)) == null
                    ) {
                        ChessMove move = new ChessMove(startPosition, new ChessPosition(1, 7), null);
                        if (
                                !doesMoveCauseCheck(move, pieceColor) &&
                                        !doesMoveCausePositionDanger(move, new ChessPosition(1, 6), pieceColor)
                        ) {
                            legalMoves.add(move);
                        }
                    }
                }
            } else if (pieceColor == TeamColor.BLACK && !blackKingHasMoved) {
                if (!blackLeftRookHasMoved) {
                    // Ensure path is clear
                    if (
                            chessBoard.getPiece(new ChessPosition(8, 4)) == null &&
                                    chessBoard.getPiece(new ChessPosition(8, 3)) == null &&
                                    chessBoard.getPiece(new ChessPosition(8, 2)) == null
                    ) {
                        ChessMove move = new ChessMove(startPosition, new ChessPosition(8, 3), null);
                        // Move cannot cause the King or Rook to be in danger
                        if (
                                !doesMoveCauseCheck(move, pieceColor) &&
                                        !doesMoveCausePositionDanger(move, new ChessPosition(8, 4), pieceColor)
                        ) {
                            legalMoves.add(move);
                        }
                    }
                }
                if (!blackRightRookHasMoved) {
                    // Ensure path is clear
                    if (
                            chessBoard.getPiece(new ChessPosition(8, 6)) == null &&
                                    chessBoard.getPiece(new ChessPosition(8, 7)) == null
                    ) {
                        ChessMove move = new ChessMove(startPosition, new ChessPosition(8, 7), null);
                        if (
                                !doesMoveCauseCheck(move, pieceColor) &&
                                        !doesMoveCausePositionDanger(move, new ChessPosition(8, 6), pieceColor)
                        ) {
                            legalMoves.add(move);
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    public ChessGame() {
        chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    public ChessGame(ChessGame other) {
        chessBoard = new ChessBoard(other.chessBoard);
        teamTurn = other.teamTurn;
        whiteKingHasMoved = other.whiteKingHasMoved;
        whiteLeftRookHasMoved = other.whiteLeftRookHasMoved;
        whiteRightRookHasMoved = other.whiteRightRookHasMoved;
        blackKingHasMoved = other.blackKingHasMoved;
        blackLeftRookHasMoved = other.blackLeftRookHasMoved;
        blackRightRookHasMoved = other.blackRightRookHasMoved;
        lastWhitePawnToDoubleMove = other.lastWhitePawnToDoubleMove;
        lastBlackPawnToDoubleMove = other.lastBlackPawnToDoubleMove;
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

        for (ChessMove move : moves) {
            if (!doesMoveCauseCheck(move, pieceColor)) {
                legalMoves.add(move);
            }
        }
        // Castling
        legalMoves.addAll(validCastlingMoves(startPosition, piece, pieceColor));
        // En Passant
        legalMoves.addAll(validEnPassantMoves(startPosition, piece));
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
                moves == null ||
                        !moves.contains(move) ||
                        piece.getTeamColor() != teamTurn
        ) {
            throw new InvalidMoveException("Invalid move");
        }

        // Reset En Passant checks
        lastWhitePawnToDoubleMove = null;
        lastBlackPawnToDoubleMove = null;

        // Castling
        // Update castling checks or castle if it was a king
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingHasMoved = true;

                // If the move was a king castling, update the rook
                if (endPosition.getColumn() - startPosition.getColumn() == 2) {
                    whiteRightRookHasMoved = true;
                    ChessPosition rookPos = new ChessPosition(1, 8);
                    chessBoard.movePiece(new ChessMove(rookPos, new ChessPosition(1, 6), null));
                } else if (endPosition.getColumn() - startPosition.getColumn() == -2) {
                    whiteLeftRookHasMoved = true;
                    ChessPosition rookPos = new ChessPosition(1, 1);
                    chessBoard.movePiece(new ChessMove(rookPos, new ChessPosition(1, 4), null));
                }
            } else {
                blackKingHasMoved = true;

                // If the move was a king castling, update the rook
                if (endPosition.getColumn() - startPosition.getColumn() == 2) {
                    blackRightRookHasMoved = true;
                    ChessPosition rookPos = new ChessPosition(8, 8);
                    chessBoard.movePiece(new ChessMove(rookPos, new ChessPosition(8, 6), null));
                } else if (piece.getTeamColor() == TeamColor.BLACK) {
                    blackLeftRookHasMoved = true;
                    ChessPosition rookPos = new ChessPosition(8, 1);
                    chessBoard.movePiece(new ChessMove(rookPos, new ChessPosition(8, 4), null));
                }
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                if (startPosition.equals(new ChessPosition(1, 1))) {
                    whiteLeftRookHasMoved = true;
                } else if (startPosition.equals(new ChessPosition(1, 8))) {
                    whiteRightRookHasMoved = true;
                }
            } else {
                if (startPosition.equals(new ChessPosition(8, 1))) {
                    blackLeftRookHasMoved = true;
                } else if (startPosition.equals(new ChessPosition(8, 8))) {
                    blackRightRookHasMoved = true;
                }
            }
        }

        // En Passant checks
        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            // If double moved, next turn enemy can en Passant
            if (Math.abs(endPosition.getRow() - startPosition.getRow()) == 2) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    lastWhitePawnToDoubleMove = endPosition;
                } else {
                    lastBlackPawnToDoubleMove = endPosition;
                }
            }

            // If move itself is en passant, remove enemy Pawn
            // If it is a diagonal move but there is no enemy piece at the diagonal, it must be En Passant
            // All horizontal moves made by a pawn are guaranteed to also be diagonal
            if (endPosition.getColumn() - startPosition.getColumn() != 0) {
                if (chessBoard.getPiece(endPosition) == null) {
                    // It must be en passant
                    int direction = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;
                    ChessPosition pawnToRemovePos = new ChessPosition(endPosition.getRow() - direction, endPosition.getColumn());
                    chessBoard.addPiece(pawnToRemovePos, null);
                }
            }
        }

        // Move piece
        chessBoard.movePiece(move);
        // Swap team color
        this.teamTurn = this.teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
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
                Collection<ChessMove> moves = validMoves(position);
                if (moves != null && !moves.isEmpty()) {
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

        ChessPosition whiteKingPos = new ChessPosition(1, 5);
        ChessPosition whiteLeftRookPos = new ChessPosition(1, 1);
        ChessPosition whiteRightRookPos = new ChessPosition(1, 8);
        ChessPosition blackKingPos = new ChessPosition(8, 5);
        ChessPosition blackLeftRookPos = new ChessPosition(8, 1);
        ChessPosition blackRightRookPos = new ChessPosition(8, 8);

        whiteKingHasMoved = board.getPiece(whiteKingPos) == null ||
                board.getPiece(whiteKingPos).getPieceType() != ChessPiece.PieceType.KING ||
                board.getPiece(whiteKingPos).getTeamColor() != TeamColor.WHITE;

        whiteLeftRookHasMoved = board.getPiece(whiteLeftRookPos) == null ||
                board.getPiece(whiteLeftRookPos).getPieceType() != ChessPiece.PieceType.ROOK ||
                board.getPiece(whiteLeftRookPos).getTeamColor() != TeamColor.WHITE;

        whiteRightRookHasMoved = board.getPiece(whiteRightRookPos) == null ||
                board.getPiece(whiteRightRookPos).getPieceType() != ChessPiece.PieceType.ROOK ||
                board.getPiece(whiteRightRookPos).getTeamColor() != TeamColor.WHITE;

        blackKingHasMoved = board.getPiece(blackKingPos) == null ||
                board.getPiece(blackKingPos).getPieceType() != ChessPiece.PieceType.KING ||
                board.getPiece(blackKingPos).getTeamColor() != TeamColor.BLACK;

        blackLeftRookHasMoved = board.getPiece(blackLeftRookPos) == null ||
                board.getPiece(blackLeftRookPos).getPieceType() != ChessPiece.PieceType.ROOK ||
                board.getPiece(blackLeftRookPos).getTeamColor() != TeamColor.BLACK;

        blackRightRookHasMoved = board.getPiece(blackRightRookPos) == null ||
                board.getPiece(blackRightRookPos).getPieceType() != ChessPiece.PieceType.ROOK ||
                board.getPiece(blackRightRookPos).getTeamColor() != TeamColor.BLACK;
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
        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition startPosition = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(startPosition);

                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }

                Collection<ChessMove> pieceMoves = piece.pieceMoves(chessBoard, startPosition);
                for (ChessMove move : pieceMoves) {
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
        // Make the move
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
        // Make the move
        chessBoard.movePiece(move);
        boolean res = isInDanger(piecePosition, teamColor);
        // Revert the move
        chessBoard.addPiece(startPosition, startPositionPiece);
        chessBoard.addPiece(endPosition, endPositionPiece);

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return whiteKingHasMoved == chessGame.whiteKingHasMoved && whiteLeftRookHasMoved == chessGame.whiteLeftRookHasMoved && whiteRightRookHasMoved == chessGame.whiteRightRookHasMoved && blackKingHasMoved == chessGame.blackKingHasMoved && blackLeftRookHasMoved == chessGame.blackLeftRookHasMoved && blackRightRookHasMoved == chessGame.blackRightRookHasMoved && Objects.equals(chessBoard, chessGame.chessBoard) && teamTurn == chessGame.teamTurn && Objects.equals(lastWhitePawnToDoubleMove, chessGame.lastWhitePawnToDoubleMove) && Objects.equals(lastBlackPawnToDoubleMove, chessGame.lastBlackPawnToDoubleMove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chessBoard, teamTurn, whiteKingHasMoved, whiteLeftRookHasMoved, whiteRightRookHasMoved, blackKingHasMoved, blackLeftRookHasMoved, blackRightRookHasMoved, lastWhitePawnToDoubleMove, lastBlackPawnToDoubleMove);
    }
}