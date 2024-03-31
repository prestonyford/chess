package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.dataModel.request.*;
import chess.dataModel.response.*;
import client.exception.ResponseException;
import client.ui.PrintConfig;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

import static client.ui.EscapeSequences.*;

public class ChessClient implements MessageHandler.Whole<String> {
    private final ServerFacade serverFacade;
    ClientOutput output;
    private State state = State.LOGGED_OUT;
    private String currentUsername = null;
    private boolean unicodePrint = true;

    public ChessClient(String domainName, ClientOutput output) {
        this.serverFacade = new ServerFacade(domainName, this);
        this.output = output;
    }

    public void eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "register": {
                    this.output.output(register(params));
                    this.output.prompt();
                    break;
                }
                case "login": {
                    this.output.output(login(params));
                    this.output.prompt();
                    break;
                }
                case "create": {
                    this.output.output(create(params));
                    this.output.prompt();
                    break;
                }
                case "list": {
                    this.output.output(list());
                    this.output.prompt();
                    break;
                }
                case "join": {
                    this.output.output(join(params));
                    break;
                }
                case "observe": {
                    this.output.output(observe(params));
                    break;
                }
                case "unicode": {
                    this.output.output(setUnicodePrint(params));
                    this.output.prompt();
                    break;
                }
                case "logout": {
                    this.output.output(logout());
                    this.output.prompt();
                    break;
                }
                case "quit": {
                    this.output.output("OK");
                    break;
                }
                default: {
                    this.output.output(help());
                    this.output.prompt();
                    break;
                }
            }
            ;
        } catch (ResponseException e) {
            this.output.output(e.getMessage());
            this.output.prompt();
        }
    }

    @Override
    public void onMessage(String s) {
        ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> printGame(new Gson().fromJson(s, LoadGame.class));
            case NOTIFICATION -> printNotification(new Gson().fromJson(s, Notification.class));
            case ERROR -> printError(new Gson().fromJson(s, Error.class));
        }
    }

    private void printGame(LoadGame message) {
        this.output.output(stringBoard(
                message.getGame().game().getBoard(),
                Objects.equals(currentUsername, message.getGame().blackUsername())
        ));
        this.output.prompt();
    }

    private void printNotification(Notification message) {
        this.output.output(message.getMessage());
        this.output.prompt();
    }

    private void printError(Error message) {
        this.output.output("ERROR: " + message.getErrorMessage());
        this.output.prompt();
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
        currentUsername = response.username();
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
        currentUsername = response.username();
        return String.format("Successfully logged in as %s", response.username());
    }

    public String logout() throws ResponseException {
        assertLoggedIn();
        serverFacade.logout();
        state = State.LOGGED_OUT;
        currentUsername = null;
        return "Successfully logged out";
    }

    public String create(String[] params) throws ResponseException {
        assertLoggedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <NAME>");
        }
        CreateGameResponse response = serverFacade.createGame(new CreateGameRequest(
                params[0]
        ));
        return String.format("Successfully created game with ID: %d", response.gameID());
    }

    public String join(String[] params) throws ResponseException {
        assertLoggedIn();
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
        ChessBoard board = new ChessBoard();
        board.resetBoard();
//        return String.format("Successfully joined game %d\n%s\n\n%s\n", gameID,
//                stringBoard(board, false),
//                stringBoard(board, true)
//        );
        return String.format("Successfully joined game %d as %s\n", gameID, params[1].toUpperCase());
    }

    public String list() throws ResponseException {
        assertLoggedIn();
        ListGamesResponse response = serverFacade.listGames();
        return response.toString();
    }

    public String observe(String[] params) throws ResponseException {
        assertLoggedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <ID>");
        }
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return String.format("Successfully joined game %s\n%s\n\n%s\n", params[0],
                stringBoard(board, false),
                stringBoard(board, true)
        );
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to log in and access games
                    unicode [TRUE|FALSE] - print with unicode if true or regular characters if false
                    quit - exit the application
                    help - what you're looking at now""";
        }
        return """
                create <NAME> - create a game with the given name
                list - list games
                join <ID> [WHITE|BLANK] - join a game
                observe <ID> - spectate a game
                logout - logout
                unicode [TRUE|FALSE] - print with unicode if true or regular characters if false
                help - what you're looking at now""";
    }

    public String setUnicodePrint(String[] params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: [TRUE|FALSE]");
        }
        String arg = params[0].toLowerCase();
        if (arg.equals("true")) {
            unicodePrint = true;
            return "Unicode ENABLED";
        } else if (arg.equals("false")) {
            unicodePrint = false;
            return "Unicode DISABLED";
        } else {
            throw new ResponseException(400, "Expected: [TRUE|FALSE]");
        }
    }

    private void assertLoggedIn() throws ResponseException {
        if (state != State.LOGGED_IN) {
            throw new ResponseException(400, "Unauthorized");
        }
    }

    private String stringBoard(ChessBoard board, boolean invert) {
        int up = invert ? 1 : 8;
        int down = invert ? 8 : 1;
        int diff = invert ? -1 : 1;

        StringBuilder sb = new StringBuilder();
        ChessGame.TeamColor tileColor = ChessGame.TeamColor.WHITE;
        stringBuildBottomTop(sb, invert);
        sb.append(RESET_BG_COLOR + '\n');
        for (int row = up; (!invert && row >= down) || (invert && row <= down); row -= diff) {
            tileColor = tileColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            stringBuildBorder(sb, (char) ('1' - 1 + row));
            for (int col = down; (!invert && col <= up) || (invert && col >= up); col += diff) {
                tileColor = tileColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                ChessGame.TeamColor teamColor = piece == null ? null : piece.getTeamColor();
                stringBuildTile(sb, tileColor, teamColor, piece == null ? null : piece.getPieceType());
            }
            stringBuildBorder(sb, (char) ('1' - 1 + row));
            sb.append(RESET_BG_COLOR + "\n");
        }
        stringBuildBottomTop(sb, invert);
        sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
        return sb.toString();
    }

    private void stringBuildBottomTop(StringBuilder sb, boolean invert) {
        int up = invert ? 1 : 8;
        int down = invert ? 8 : 1;
        char diff = (char) (invert ? -1 : 1);
        stringBuildBorder(sb, ' ');
        for (char i = (char) ('A' - 1 + down); (!invert && i < 'A' + up) || (invert && i >= 'A' - 1 + up); i += diff) {
            stringBuildBorder(sb, i);
        }
        stringBuildBorder(sb, ' ');
    }

    private void stringBuildBorder(StringBuilder sb, char symbol) {
        sb.append(SET_BG_COLOR_BORDER + SET_TEXT_COLOR_WHITE);
        sb.append("\u2005\u2005");
        sb.append(symbol);
        sb.append("\u2005\u2005");
    }

    private void stringBuildTile(StringBuilder sb, ChessGame.TeamColor tileColor, ChessGame.TeamColor teamColor, ChessPiece.PieceType piece) {
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

        if (unicodePrint) {
            stringBuildPiece(sb, teamColor, piece, PrintConfig.unicode);
        } else {
            stringBuildPiece(sb, teamColor, piece, PrintConfig.alphabetic);
        }
    }

    private void stringBuildPiece(StringBuilder sb, ChessGame.TeamColor teamColor, ChessPiece.PieceType piece, PrintConfig config) {
        if (piece != null) {
            switch (piece) {
                case KING:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? config.whiteKing() : config.blackKing());
                    break;
                case QUEEN:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? config.whiteQueen() : config.blackQueen());
                    break;
                case KNIGHT:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? config.whiteKnight() : config.blackKnight());
                    break;
                case BISHOP:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? config.whiteBishop() : config.blackBishop());
                    break;
                case ROOK:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? config.whiteRook() : config.blackRook());
                    break;
                case PAWN:
                    sb.append(teamColor == ChessGame.TeamColor.WHITE ? config.whitePawn() : config.blackPawn());
                    break;
            }
        } else {
            sb.append(config.empty());
        }
    }
}