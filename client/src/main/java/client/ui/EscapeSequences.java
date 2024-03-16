package client.ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    public static final String UNICODE_ESCAPE = "\u001b";
    public static final String ANSI_ESCAPE = "\033";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";

    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_TEXT_COLOR = UNICODE_ESCAPE + "[0m";

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";


    public static final String SET_BG_COLOR_BEIGE = SET_BG_COLOR + "222m";
    public static final String SET_BG_COLOR_BROWN = SET_BG_COLOR + "136m";
    public static final String SET_BG_COLOR_BORDER = SET_BG_COLOR + "94m";

    public static final String RESET_BG_COLOR = UNICODE_ESCAPE + "[49m";


    // Unicode chess pieces
    public static final String WHITE_KING = "\u2004♔\u2004";
    public static final String WHITE_QUEEN = "\u2004♕\u2004";
    public static final String WHITE_BISHOP = "\u2004♗\u2004";
    public static final String WHITE_KNIGHT = "\u2004♘\u2004";
    public static final String WHITE_ROOK = "\u2004♖\u2004";
    public static final String WHITE_PAWN = "\u2004♙\u2004";
    public static final String BLACK_KING = "\u2004♚\u2004";
    public static final String BLACK_QUEEN = "\u2004♛\u2004";
    public static final String BLACK_BISHOP = "\u2004♝\u2004";
    public static final String BLACK_KNIGHT = "\u2004♞\u2004";
    public static final String BLACK_ROOK = "\u2004♜\u2004";
    public static final String BLACK_PAWN = "\u2004♟\u2004";
    public static final String EMPTY = "\u2004\u2003\u2004";

    // Alphabetic chess pieces
    public static final String WHITE_KING_ALPHA = SET_TEXT_BOLD + "\u2005\u2005K\u2005\u2005" + RESET_TEXT_BOLD_FAINT;
    public static final String WHITE_QUEEN_ALPHA = SET_TEXT_BOLD + "\u2005\u2005Q\u2005\u2005" + RESET_TEXT_BOLD_FAINT;
    public static final String WHITE_BISHOP_ALPHA = SET_TEXT_BOLD + "\u2005\u2005B\u2005\u2005" + RESET_TEXT_BOLD_FAINT;
    public static final String WHITE_KNIGHT_ALPHA = SET_TEXT_BOLD + "\u2005\u2005N\u2005\u2005" + RESET_TEXT_BOLD_FAINT;
    public static final String WHITE_ROOK_ALPHA = SET_TEXT_BOLD + "\u2005\u2005R\u2005\u2005" + RESET_TEXT_BOLD_FAINT;
    public static final String WHITE_PAWN_ALPHA = SET_TEXT_BOLD + "\u2005\u2005P\u2005\u2005" + RESET_TEXT_BOLD_FAINT;
    public static final String BLACK_KING_ALPHA = WHITE_KING_ALPHA;
    public static final String BLACK_QUEEN_ALPHA = WHITE_QUEEN_ALPHA;
    public static final String BLACK_BISHOP_ALPHA = WHITE_BISHOP_ALPHA;
    public static final String BLACK_KNIGHT_ALPHA = WHITE_KNIGHT_ALPHA;
    public static final String BLACK_ROOK_ALPHA = WHITE_ROOK_ALPHA;
    public static final String BLACK_PAWN_ALPHA = WHITE_PAWN_ALPHA;
    public static final String EMPTY_ALPHA = "\u2005\u2005 \u2005\u2005";

    public static String moveCursorToLocation(int x, int y) {
        return UNICODE_ESCAPE + "[" + y + ";" + x + "H";
    }
}
