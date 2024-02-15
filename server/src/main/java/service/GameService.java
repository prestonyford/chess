package service;

import chess.ChessGame;
import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.response.CreateGameResponse;
import dataAccess.DataAccessException;
import service.exceptions.ServiceException;

public class GameService extends Service {
    private static final GameService INSTANCE = new GameService();
    private GameService() {}
    public static GameService getInstance() {
        return INSTANCE;
    }

    public static class IDGen {
        private static int latestID = 1;
        public static int newID() {
            return latestID++;
        }
    }
    public CreateGameResponse createGame(String authToken, CreateGameRequest createGameRequest) throws ServiceException, DataAccessException {
        if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
            throw new ServiceException(400, "Error: bad request");
        }
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        // Create the game
        GameData newGame = new GameData(
                IDGen.newID(),
                null,
                null,
                createGameRequest.gameName(),
                new ChessGame()
        );
        // Insert into database
        db.insertGame(newGame);

        return new CreateGameResponse(newGame.gameID());
    }
}
