package client;

import chess.dataModel.request.*;
import chess.dataModel.response.*;
import client.exception.ResponseException;

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

    public String list() {
        return "";
    }

    public String join(String[] params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <ID>");
        }
        return "";
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
}