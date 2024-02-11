package server;

import service.ApplicationService;
import service.GameService;
import service.UserService;
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
            return "";
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
