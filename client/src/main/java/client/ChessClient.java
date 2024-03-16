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
        for (int row = 1; row <= 8; ++row) {
            tileColor = tileColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            for (int col = 1; col <= 8; ++col) {
                tileColor = tileColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                ChessGame.TeamColor teamColor = piece == null ? null : piece.getTeamColor();
                tileCell(sb, tileColor, teamColor, piece == null ? null : piece.getPieceType());
            }
            sb.append(RESET_BG_COLOR + "\n");
        }
        return sb.toString();
    }

    public String stringOppositeBoard() {
        return "";
    }

    private void coordCell(StringBuilder sb, char c) {
        sb.append(SET_BG_COLOR_LIGHT_GREY);
        sb.append(SET_TEXT_COLOR_BLACK);
        sb.append(c);
    }

    private void tileCell(StringBuilder sb, ChessGame.TeamColor tileColor, ChessGame.TeamColor teamColor, ChessPiece.PieceType piece) {
        if (tileColor == ChessGame.TeamColor.WHITE) {
            sb.append(SET_BG_COLOR_WHITE);
        } else {
            sb.append(SET_BG_COLOR_BLACK);
        }
        if (teamColor == ChessGame.TeamColor.WHITE) {
            sb.append(SET_TEXT_COLOR_RED);
        } else {
            sb.append(SET_TEXT_COLOR_BLUE);
        }

        if (piece != null) {
            switch (piece) {
                case KING:
                    sb.append('K');
                case QUEEN:
                    sb.append('Q');
                case KNIGHT:
                    sb.append('N');
                case BISHOP:
                    sb.append('B');
                case ROOK:
                    sb.append('R');
                case PAWN:
                    sb.append('P');
                default:
                    sb.append(' ');
            }
        } else {
            sb.append(' ');
        }

    }
}