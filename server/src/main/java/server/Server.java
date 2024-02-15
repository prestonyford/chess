package server;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.LoginRequest;
import chess.dataModel.request.LogoutRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.CreateGameResponse;
import chess.dataModel.response.LoginResponse;
import chess.dataModel.response.RegisterResponse;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import service.ApplicationService;
import service.GameService;
import service.UserService;
import service.exceptions.ServiceException;
import spark.*;

import java.util.Map;
import java.util.Objects;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        UserService userService = new UserService();
        GameService gameService = new GameService();
        ApplicationService applicationService = new ApplicationService();

        // Register your endpoints and handle exceptions here.

        // Handle all endpoint ServiceExceptions
        Spark.exception(ServiceException.class, (ex, req, res) -> {
            if (Objects.equals(req.pathInfo(), "/session") && req.requestMethod().equals("DELETE")) {
                System.out.println(req.pathInfo());
            }

            String body = new Gson().toJson(Map.of("message", ex.getMessage()));
            res.type("application/json");
            res.body(body);
            res.status(ex.getCode());
        });
        // Handle DataAccess exceptions
        Spark.exception(DataAccessException.class, (ex, req, res) -> {
            res.status(500);
        });

        Spark.delete("/db", (req, res) -> {
            applicationService.bigRedButton();
            res.status(200);
            res.body("");
            return "";
        });

        Spark.post("/user", (req, res) -> {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResponse registerResponse = userService.register(registerRequest);
            String body = new Gson().toJson(registerResponse);
            res.status(200);
            res.type("application/json");
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
            System.out.println(req.headers());
            LogoutRequest logoutRequest = new LogoutRequest(req.headers("Authorization"));
            userService.logout(logoutRequest);
            res.status(200);
            res.body("");
            return "";
        });

        Spark.post("/game", (req, res) -> {
            CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
            createGameRequest.setAuthToken(req.headers("Authorization"));
            CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
            String body = new Gson().toJson(createGameResponse);
            res.body(body);
            return body;
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
