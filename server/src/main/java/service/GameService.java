package service;

import chess.ChessGame;
import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.response.CreateGameResponse;
import dataAccess.DataAccessException;
import service.exceptions.ServiceException;

import java.util.Objects;

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
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
            throw new ServiceException(400, "Error: bad request");
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
    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ServiceException, DataAccessException {
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        if (joinGameRequest.gameID() == 0) {
            throw new ServiceException(400, "Error: bad request");
        }
        GameData gameData = db.getGame(joinGameRequest.gameID());
        GameData updatedGame;
        if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
            if (gameData.whiteUsername() != null) {
                throw new ServiceException(403, "Error: already taken");
            }
            updatedGame = new GameData(
                    gameData.gameID(),
                    auth.username(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    new ChessGame(gameData.game())
            );
        }
        else if (Objects.equals(joinGameRequest.playerColor(), "BLACK")) {
            if (gameData.blackUsername() != null) {
                throw new ServiceException(403, "Error: already taken");
            }
            updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    auth.username(),
                    gameData.gameName(),
                    new ChessGame(gameData.game())
            );
        }
        else {
            throw new ServiceException(400, "Error: bad request");
        }
        db.updateGame(gameData.gameID(), updatedGame);
    }
}
