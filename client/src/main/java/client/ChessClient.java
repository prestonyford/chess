package client;

import chess.*;
import chess.dataModel.GameData;
import chess.dataModel.request.*;
import chess.dataModel.response.*;
import client.exception.ResponseException;
import client.ui.PrintConfig;
import com.google.gson.Gson;
import org.glassfish.tyrus.core.WebSocketException;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.MessageHandler;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static client.ui.EscapeSequences.*;

public class ChessClient implements MessageHandler.Whole<String> {
    private final ServerFacade serverFacade;
    ClientOutput output;
    private State state = State.LOGGED_OUT;
    private String currentUsername = null;
    private GameData latestGame = null;
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
                    this.output.output(register(params) + '\n');
                    this.output.prompt();
                    break;
                }
                case "login": {
                    this.output.output(login(params) + '\n');
                    this.output.prompt();
                    break;
                }
                case "create": {
                    this.output.output(create(params) + '\n');
                    this.output.prompt();
                    break;
                }
                case "list": {
                    this.output.output(list() + '\n');
                    this.output.prompt();
                    break;
                }
                case "join": {
                    this.output.output(join(params) + '\n');
                    break;
                }
                case "observe": {
                    this.output.output(observe(params) + '\n');
                    break;
                }
                case "unicode": {
                    this.output.output(setUnicodePrint(params) + '\n');
                    this.output.prompt();
                    break;
                }
                case "logout": {
                    this.output.output(logout() + '\n');
                    this.output.prompt();
                    break;
                }
                case "quit": {
                    this.output.output("OK" + '\n');
                    break;
                }
                case "redraw": {
                    this.output.output(redraw() + '\n');
                    this.output.prompt();
                    break;
                }
                case "move": {
                    move(params);
                    break;
                }
                case "highlight": {
                    this.output.output(highlight(params) + '\n');
                    this.output.prompt();
                    break;
                }
                case "leave": {
                    this.output.output(leave() + '\n');
                    this.output.prompt();
                    break;
                }
                case "resign": {
                    resign();
                    break;
                }
                default: {
                    this.output.output(help() + '\n');
                    this.output.prompt();
                    break;
                }
            }
            ;
        } catch (ResponseException e) {
            this.output.output(e.getMessage() + '\n');
            this.output.prompt();
        }
    }

    @Override
    public void onMessage(String s) {
        ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
        switch (message.getServerMessageType()) {
            case LOAD_GAME: {
                LoadGame loadGame = new Gson().fromJson(s, LoadGame.class);
                printGame(loadGame);
                latestGame = loadGame.getGame();
                break;
            }
            case NOTIFICATION: {
                printNotification(new Gson().fromJson(s, Notification.class));
                break;
            }
            case ERROR: {
                printError(new Gson().fromJson(s, Error.class));
                break;
            }
        }
    }

    private void printGame(LoadGame message) {
        this.output.output('\r' + stringBoard(
                message.getGame().game().getBoard(),
                List.of(), Objects.equals(currentUsername, message.getGame().blackUsername())
        ) + '\n');
        this.output.prompt();
    }

    private void printNotification(Notification message) {
        this.output.output(SET_TEXT_COLOR_BLUE + '\r' + message.getMessage());
        this.output.prompt();
    }

    private void printError(Error message) {
        this.output.output("ERROR: " + message.getErrorMessage() + '\n');
        this.output.prompt();
    }

    private String register(String[] params) throws ResponseException {
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

    private String login(String[] params) throws ResponseException {
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

    private String logout() throws ResponseException {
        assertLoggedIn();
        serverFacade.logout();
        state = State.LOGGED_OUT;
        currentUsername = null;
        return "Successfully logged out";
    }

    private String create(String[] params) throws ResponseException {
        assertLoggedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <NAME>");
        }
        CreateGameResponse response = serverFacade.createGame(new CreateGameRequest(
                params[0]
        ));
        return String.format("Successfully created game with ID: %d", response.gameID());
    }

    private String join(String[] params) throws ResponseException {
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
        state = State.PLAYING;
        return String.format("Successfully joined game %d as %s\n", gameID, params[1].toUpperCase());
    }

    private String list() throws ResponseException {
        assertLoggedIn();
        ListGamesResponse response = serverFacade.listGames();
        return response.toString().replaceAll("<empty>", SET_TEXT_COLOR_WHITE + "<empty>" + SET_TEXT_COLOR_BLUE);
    }

    private String observe(String[] params) throws ResponseException {
        assertLoggedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <ID>");
        }
        serverFacade.observeGame(Integer.parseInt(params[0]));
        state = State.OBSERVING;
        return String.format("Observing game %s\n", params[0]);
    }

    private String redraw() throws ResponseException {
        try {
            assertPlaying();
        } catch (ResponseException ignore) {
            assertObserving();
        }
        return stringBoard(
                latestGame.game().getBoard(),
                List.of(), Objects.equals(currentUsername, latestGame.blackUsername())
        );
    }

    private void move(String[] params) throws ResponseException {
        assertPlaying();
        if (params.length < 2 || params.length > 3) {
            throw new ResponseException(400, "Expected: <START_POSITION> <END_POSITION> [QUEEN|ROOK|BISHOP|KNIGHT|blank]");
        }
        ChessMove move = new ChessMove(
                getPositionFromInput(params[0]),
                getPositionFromInput(params[1]),
                params.length < 3 ? null : switch (params[2].toLowerCase()) {
                    case "queen" -> ChessPiece.PieceType.QUEEN;
                    case "rook" -> ChessPiece.PieceType.ROOK;
                    case "bishop" -> ChessPiece.PieceType.BISHOP;
                    case "pawn" -> ChessPiece.PieceType.PAWN;
                    default -> null;
                }
        );
        serverFacade.makeMove(latestGame.gameID(), move);
    }

    private String highlight(String[] params) throws ResponseException {
        ChessPosition pos = getPositionFromInput(params[0]);
        Collection<ChessMove> moves = latestGame.game().validMoves(pos);
        return stringBoard(
                latestGame.game().getBoard(),
                moves == null ? List.of() : moves.stream().map(ChessMove::getEndPosition).collect(Collectors.toList()),
                Objects.equals(currentUsername, latestGame.blackUsername())
        );
    }

    private String leave() throws ResponseException {
        try {
            assertPlaying();
        } catch (ResponseException ignore) {
            assertObserving();
        }
        serverFacade.leaveGame(latestGame.gameID());
        state = State.LOGGED_IN;
        return String.format("Left game %d", latestGame.gameID());
    }

    private void resign() throws ResponseException {
        assertPlaying();
        serverFacade.resign(latestGame.gameID());
    }

    private String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to log in and access games
                    unicode [TRUE|FALSE] - print with unicode if true or regular characters if false
                    quit - exit the application
                    help - what you're looking at now""";
        } else if (state == State.LOGGED_IN) {
            return """
                    create <NAME> - create a game with the given name
                    list - list games
                    join <ID> [WHITE|BLANK] - join a game
                    observe <ID> - spectate a game
                    logout - logout
                    unicode [TRUE|FALSE] - print with unicode if true or regular characters if false
                    help - what you're looking at now""";
        } else if (state == State.PLAYING) {
            return """
                    redraw - redraw the chess board
                    move <START_POSITION> <END_POSITION> [QUEEN|ROOK|BISHOP|KNIGHT|blank] - make a move (i.e. move C2 C4). Include the promotion piece as an argument when necessary.
                    highlight <position> - show the valid moves of the piece at the given position (i.e. highlight C4)
                    unicode [TRUE|FALSE] - print with unicode if true or regular characters if false
                    leave - leave the game
                    resign - forfeit the game
                    help - what you're looking at now""";
        }
        return """
                redraw - redraw the chess board
                highlight <position> - show the valid moves of the piece at the given position (i.e. highlight C4)
                leave - leave the game
                help - what you're looking at now""";
    }

    private String setUnicodePrint(String[] params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: [TRUE|FALSE]");
        }
        String arg = params[0].toLowerCase();
        if (arg.equals("true") || arg.equals("on")) {
            unicodePrint = true;
            return "Unicode ENABLED";
        } else if (arg.equals("false") || arg.equals("off")) {
            unicodePrint = false;
            return "Unicode DISABLED";
        } else {
            throw new ResponseException(400, "Expected: [TRUE|FALSE]");
        }
    }

    private ChessPosition getPositionFromInput(String input) throws ResponseException {
        String regex = "^([a-h])([1-8])$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input.toLowerCase());
        if (matcher.find()) {
            return new ChessPosition(Integer.parseInt(matcher.group(2)), matcher.group(1).charAt(0) - 'a' + 1);
        }
        throw new ResponseException(400, "Expected: <start_position> <end_position>");
    }

    private void assertLoggedIn() throws ResponseException {
        if (state != State.LOGGED_IN) {
            throw new ResponseException(400, "Unauthorized");
        }
    }

    private void assertPlaying() throws ResponseException {
        if (state != State.PLAYING) {
            throw new ResponseException(400, "You are not playing a game");
        }
    }

    private void assertObserving() throws ResponseException {
        if (state != State.OBSERVING) {
            throw new ResponseException(400, "You are not observing a game");
        }
    }

    private String stringBoard(ChessBoard board, Collection<ChessPosition> highlight, boolean invert) {
        int up = invert ? 1 : 8;
        int down = invert ? 8 : 1;
        int diff = invert ? -1 : 1;

        StringBuilder sb = new StringBuilder();
        boolean tileAlternate = true;
        stringBuildBottomTop(sb, invert);
        sb.append(RESET_BG_COLOR + '\n');
        for (int row = up; (!invert && row >= down) || (invert && row <= down); row -= diff) {
            tileAlternate = !tileAlternate;
            stringBuildBorder(sb, (char) ('1' - 1 + row));
            for (int col = down; (!invert && col <= up) || (invert && col >= up); col += diff) {
                tileAlternate = !tileAlternate;
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                ChessGame.TeamColor teamColor = piece == null ? null : piece.getTeamColor();
                stringBuildTile(
                        sb, tileAlternate, teamColor,
                        piece == null ? null : piece.getPieceType(),
                        highlight.contains(new ChessPosition(row, col))
                );
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

    private void stringBuildTile(StringBuilder sb, boolean tileAlternate, ChessGame.TeamColor teamColor, ChessPiece.PieceType piece, boolean highlight) {
        if (highlight) {
            sb.append(SET_BG_COLOR_GREEN);
        } else if (tileAlternate) {
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