package server;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.CreateGameResponse;
import chess.dataModel.response.ListGamesResponse;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.SQLDataAccess;
import server.websocket.WebSocketHandler;
import service.ApplicationService;
import service.GameService;
import service.UserService;
import service.exceptions.ServiceException;
import spark.*;

import java.util.Map;

public class Server {
    public int run(int desiredPort) {
        try {
            Spark.port(desiredPort);
            Spark.staticFiles.location("web");

            // Database and services
            DataAccess db = new SQLDataAccess();
            UserService userService = new UserService(db);
            GameService gameService = new GameService(db);
            ApplicationService applicationService = new ApplicationService(db);
            WebSocketHandler webSocketHandler = new WebSocketHandler(db);

            // Handle all endpoint ServiceExceptions
            Spark.exception(ServiceException.class, (ex, req, res) -> {
                String body = new Gson().toJson(Map.of("message", ex.getMessage()));
                res.type("application/json");
                res.body(body);
                res.status(ex.getCode());
            });
            // Handle all endpoint DataAccess exceptions
            Spark.exception(DataAccessException.class, (ex, req, res) -> {
                res.status(500);
                res.body(ex.getMessage());
            });

            Spark.webSocket("/connect", webSocketHandler);

            // Handle endpoints
            Spark.delete("/db", (req, res) -> {
                applicationService.bigRedButton();
                res.status(200);
                res.body("");
                return "";
            });

            Spark.post("/user", (req, res) -> {
                RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
                RegisterResponse registerResponse = userService.register(registerRequest);
                res.status(200);
                res.type("application/json");
                String body = new Gson().toJson(registerResponse);
                res.body(body);
                return body;
            });

            Spark.post("/session", (req, res) -> {
                LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
                LoginResponse loginResponse = userService.login(loginRequest);
                res.status(200);
                res.type("application/json");
                String body = new Gson().toJson(loginResponse);
                res.body(body);
                return body;
            });

            Spark.delete("/session", (req, res) -> {
                userService.logout(req.headers("Authorization"));
                res.status(200);
                res.body("");
                return "";
            });

            Spark.post("/game", (req, res) -> {
                CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
                CreateGameResponse createGameResponse = gameService.createGame(req.headers("Authorization"), createGameRequest);
                res.status(200);
                res.type("application/json");
                String body = new Gson().toJson(createGameResponse);
                res.body(body);
                return body;
            });

            Spark.put("/game", (req, res) -> {
                JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
                gameService.joinGame(req.headers("Authorization"), joinGameRequest);
                res.status(200);
                res.body("");
                return "";
            });

            Spark.get("/game", (req, res) -> {
                ListGamesResponse listGamesResponse = gameService.listGames(req.headers("Authorization"));
                res.status(200);
                res.type("application/json");
                String body = new Gson().toJson(listGamesResponse);
                res.body(body);
                return body;
            });

            Spark.awaitInitialization();
            return Spark.port();
        } catch (DataAccessException ex) {
            System.out.println("Could not start server: " + ex.getMessage());
        }
        return 0;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
