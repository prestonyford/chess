package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.dataModel.request.*;
import chess.dataModel.response.*;
import client.exception.ResponseException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static client.ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = State.LOGGED_OUT;

    public ChessClient(String serverUrl) throws MalformedURLException, URISyntaxException {
        serverFacade = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String register(String[] params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
        }
        RegisterResponse response = serverFacade.register(new RegisterRequest(
                params[0],
                params[1],
                params[2]
        ));
        state = State.LOGGED_IN;
        return String.format("Successfully created user: %s", response.username());
    }

    public String login(String[] params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
        }
        LoginResponse response = serverFacade.login(new LoginRequest(
                params[0],
                params[1]
        ));
        state = State.LOGGED_IN;
        return String.format("Successfully logged in as %s", response.username());
    }

    public String logout() throws ResponseException {
        serverFacade.logout();
        state = State.LOGGED_OUT;
        return "Successfully logged out";
    }

    public String create(String[] params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <NAME>");
        }
        CreateGameResponse response = serverFacade.createGame(new CreateGameRequest(
                params[0]
        ));
        return String.format("Successfully created game with ID: %d", response.gameID());
    }

    public String join(String[] params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLANK|<empty>]");
        }
        int gameID;
        try {
            gameID = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLANK|<empty>]");
        }
        serverFacade.joinGame(new JoinGameRequest(
                params[1],
                gameID
        ));
        return String.format("Successfully joined game %d\n%s", gameID, stringBoard(new ChessBoard()));
    }

    public String list() throws ResponseException {
        ListGamesResponse response = serverFacade.listGames();
        return response.toString();
    }

    public String observe(String[] params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <ID>");
        }
        return "";
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to log in and access games
                    quit - exit the application
                    help - what you're looking at now""";
        }
        return """
                create <NAME> - create a game with the given name
                list - list games
                join <ID> [WHITE|BLANK|<empty>] - join a game
                observe <ID> - spectate a game
                logout - logout
                quit - quit
                help - what you're looking at now""";
    }

    public String stringBoard(ChessBoard board) {
        StringBuilder sb = new StringBuilder();
        ChessGame.TeamColor tileColor = ChessGame.TeamColor.WHITE;
        borderCell(sb, ' ');
        for (char i = 'A'; i < 'A' + 8; ++i) {
            borderCell(sb, i);
        }
        borderCell(sb, ' ');
        sb.append(RESET_BG_COLOR + '\n');
        for (int row = 8; row >= 1; --row) {
            tileColor = tileColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            borderCell(sb, (char) ('1' - 1 + row));
            for (int col = 1; col <= 8; ++col) {
                tileColor = tileColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                ChessGame.TeamColor teamColor = piece == null ? null : piece.getTeamColor();
                tileCell(sb, tileColor, teamColor, piece == null ? null : piece.getPieceType());
            }
            borderCell(sb, (char) ('1' - 1 + row));
            sb.append(RESET_BG_COLOR + "\n");
        }
        borderCell(sb, ' ');
        for (char i = 'A'; i < 'A' + 8; ++i) {
            borderCell(sb, i);
        }
        borderCell(sb, ' ');
        sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
        return sb.toString();
    }

    public String stringOppositeBoard() {
        return "";
    }

    private void borderCell(StringBuilder sb, char symbol) {
        sb.append(SET_BG_COLOR_BORDER + SET_TEXT_COLOR_WHITE);
        sb.append("\u2005\u2005");
        sb.append(symbol);
        sb.append("\u2005\u2005");
    }

    private void coordCell(StringBuilder sb, char c) {
        sb.append(SET_BG_COLOR_LIGHT_GREY);
        sb.append(SET_TEXT_COLOR_BLACK);
        sb.append(c);
    }

    private void tileCell(StringBuilder sb, ChessGame.TeamColor tileColor, ChessGame.TeamColor teamColor, ChessPiece.PieceType piece) {
        if (tileColor == ChessGame.TeamColor.WHITE) {
            sb.append(SET_BG_COLOR_BEIGE);
        } else {
            sb.append(SET_BG_COLOR_BROWN);
        }
        if (teamColor == ChessGame.TeamColor.WHITE) {
            sb.append(SET_TEXT_COLOR_WHITE);
        } else {
            sb.append(SET_TEXT_COLOR_BLACK);
        }

        if (piece != null) {
            switch (piece) {
                case KING:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING);
                    break;
                case QUEEN:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN);
                    break;
                case KNIGHT:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT);
                    break;
                case BISHOP:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP);
                    break;
                case ROOK:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK);
                    break;
                case PAWN:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN);
                    break;
            }
        } else {
            sb.append(EMPTY);
        }
    }
}