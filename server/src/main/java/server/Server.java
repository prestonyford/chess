package server;

import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.RegisterResponse;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import service.ApplicationService;
import service.GameService;
import service.UserService;
import service.exceptions.ServiceException;
import service.exceptions.UserAlreadyTakenException;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        UserService userService = new UserService();
        GameService gameService = new GameService();
        ApplicationService applicationService = new ApplicationService();

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> {
            applicationService.clearDatabase();
            res.status(200);
            res.body("");
            return "";
        });

        Spark.post("/user", (req, res) -> {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            String body;
            try {
                RegisterResponse registerResponse = userService.register(registerRequest);
                body = new Gson().toJson(registerResponse);
                res.status(200);
            }
            catch (ServiceException ex) {
                body = ex.getMessage();
                res.status(ex.getCode());
            }
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
