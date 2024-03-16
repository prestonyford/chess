package client.ui;

import static client.ui.EscapeSequences.*;

public record PrintConfig(String whiteKing, String blackKing, String whiteQueen, String blackQueen, String whiteKnight,
                          String blackKnight, String whiteBishop, String blackBishop, String whiteRook,
                          String blackRook, String whitePawn, String blackPawn, String empty) {
    public static PrintConfig unicode = new PrintConfig(
            WHITE_KING, BLACK_KING, WHITE_QUEEN, BLACK_QUEEN,
            WHITE_KNIGHT, BLACK_KNIGHT, WHITE_BISHOP, BLACK_BISHOP,
            WHITE_ROOK, BLACK_ROOK, WHITE_PAWN, BLACK_PAWN, EMPTY
    );
    public static PrintConfig alphabetic = new PrintConfig(
            WHITE_KING_ALPHA, BLACK_KING_ALPHA, WHITE_QUEEN_ALPHA, BLACK_QUEEN_ALPHA,
            WHITE_KNIGHT_ALPHA, BLACK_KNIGHT_ALPHA, WHITE_BISHOP_ALPHA, BLACK_BISHOP_ALPHA,
            WHITE_ROOK_ALPHA, BLACK_ROOK_ALPHA, WHITE_PAWN_ALPHA, BLACK_PAWN_ALPHA, EMPTY_ALPHA
    );
}
