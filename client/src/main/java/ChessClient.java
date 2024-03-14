import chess.ChessGame;
import exception.ResponseException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;

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

    public String register(String[] params) {
        return "";
    }

    public String login(String[] params) {
        return "";
    }

    public String create(String[] params) {
        return "";
    }

    public String list() {
        return "";
    }

    public String join(String[] params) {
        return "";
    }

    public String observe(String[] params) {
        return "";
    }

    public String logout() {
        return "";
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to log in and access games
                    quit - exit the application
                    help - what you're looking at now
                    """;
        }
        return """
                create <NAME> - create a game with the given name
                list - list games
                join <ID> [WHITE|BLANK|<empty>] - join a game
                observe <ID> - spectate a game
                logout - logout
                quit - quit
                help - what you're looking at now
                """;
    }
}