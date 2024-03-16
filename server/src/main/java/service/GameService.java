package service;

import chess.ChessGame;
import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.response.CreateGameResponse;
import chess.dataModel.response.ListGamesResponse;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import service.exceptions.ServiceException;

import java.util.Objects;

public class GameService extends Service {
    public GameService(DataAccess db) {
        super(db);
    }

    public CreateGameResponse createGame(String authToken, CreateGameRequest createGameRequest) throws ServiceException, DataAccessException {
        verifyAuthToken(authToken);
        verifyRequestFields(createGameRequest);

        // Create the game
        GameData newGame = new GameData(
                null,
                null,
                null,
                createGameRequest.gameName(),
                new ChessGame()
        );
        // Insert into database
        newGame = db.insertGame(newGame);

        return new CreateGameResponse(newGame.gameID());
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ServiceException, DataAccessException {
        verifyAuthToken(authToken);
        // Do not call verifyRequestFields because an empty playerColor is valid
        AuthData auth = db.getAuth(authToken);
        GameData gameData = db.getGame(joinGameRequest.gameID());

        if (gameData == null) {
            throw new ServiceException(400, "Error: bad request");
        }

        GameData updatedGame;

        if (joinGameRequest.playerColor() != null && Objects.equals(joinGameRequest.playerColor().toUpperCase(), "WHITE")) {
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
        } else if (joinGameRequest.playerColor() != null && Objects.equals(joinGameRequest.playerColor().toUpperCase(), "BLACK")) {
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
        } else {
            // User is an observer, add functionality in phase 6
            updatedGame = gameData;
        }
        db.updateGame(gameData.gameID(), updatedGame);
    }

    public ListGamesResponse listGames(String authToken) throws ServiceException, DataAccessException {
        verifyAuthToken(authToken);
        return new ListGamesResponse(db.listGames().toArray(new GameData[0]));
    }
}
