package server.websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.InvalidMoveException;
import chess.dataModel.AuthData;
import chess.dataModel.GameData;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final DataAccess db;

    // Mapping of gameID to its ConnectionManager
    private final ConcurrentHashMap<Integer, ConnectionManager> gameRooms = new ConcurrentHashMap<Integer, ConnectionManager>();

    public WebSocketHandler(DataAccess db) {
        this.db = db;
    }

    private void addConnectionToRoom(int gameID, Connection connection) {
        if (!gameRooms.containsKey(gameID)) {
            gameRooms.put(gameID, new ConnectionManager());
        }
        gameRooms.get(gameID).add(connection);
    }

    private void assertAuthorizedGameUpdate(GameData gameData, String username, ChessGame.TeamColor teamColor) throws WebSocketException {
        if (
                (teamColor == ChessGame.TeamColor.WHITE && !Objects.equals(gameData.whiteUsername(), username)) ||
                        (teamColor == ChessGame.TeamColor.BLACK && !Objects.equals(gameData.blackUsername(), username))
        ) {
            throw new WebSocketException("Position is occupied");
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String s) throws DataAccessException, IOException {
        UserGameCommand command = new Gson().fromJson(s, UserGameCommand.class);
        AuthData user = db.getAuth(command.getAuthString());
        Connection connection = new Connection(user.username(), session);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(connection, new Gson().fromJson(s, JoinPlayer.class));
            case JOIN_OBSERVER -> joinObserver(connection, new Gson().fromJson(s, JoinObserver.class));
            case MAKE_MOVE -> makeMove(connection, new Gson().fromJson(s, MakeMove.class));
            case LEAVE -> leave(connection, new Gson().fromJson(s, Leave.class));
            // case RESIGN ->
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) throws IOException {
        System.out.println(error.getMessage());
        session.getRemote().sendString(
                new Gson().toJson(new Error(
                        error.getMessage()
                ))
        );
    }

    private void joinPlayer(Connection connection, JoinPlayer message) throws WebSocketException {
        try {
            GameData game = db.getGame(message.getGameID());
            assertAuthorizedGameUpdate(game, connection.visitorName, message.getPlayerColor());

            addConnectionToRoom(message.getGameID(), connection);
            connection.send(new Gson().toJson(new LoadGame(game)));
            gameRooms.get(message.getGameID()).broadcast(connection.visitorName, new Notification(
                    connection.visitorName + " joined as " + message.getPlayerColor().name()
            ));
        } catch (IOException | DataAccessException ex) {
            throw new WebSocketException(ex.getMessage());
        }
    }

    private void joinObserver(Connection connection, JoinObserver message) throws WebSocketException {
        try {
            GameData game = db.getGame(message.getGameID());
            if (game == null) {
                throw new WebSocketException("Nonexistent gameID");
            }
            addConnectionToRoom(message.getGameID(), connection);
            connection.send(new Gson().toJson(new LoadGame(game)));
            gameRooms.get(message.getGameID()).broadcast(connection.visitorName, new Notification(
                    connection.visitorName + " joined as an observer"
            ));
        } catch (IOException | DataAccessException ex) {
            throw new WebSocketException(ex.getMessage());
        }
    }

    private void makeMove(Connection connection, MakeMove message) throws WebSocketException {
        try {
            GameData game = db.getGame(message.getGameID());
            ChessPiece piece = game.game().getBoard().getPiece(message.getMove().getStartPosition());

            assertAuthorizedGameUpdate(game, connection.visitorName,
                    piece.getTeamColor()
            );

            game.game().makeMove(message.getMove());
            db.updateGame(game.gameID(), game);
            gameRooms.get(message.getGameID()).broadcast("", new LoadGame(game));
            gameRooms.get(message.getGameID()).broadcast(connection.visitorName, new Notification(
                    String.format("%s moved %s at %s to %s",
                            connection.visitorName,
                            piece.getPieceType().name(),
                            message.getMove().getStartPosition().toString(),
                            message.getMove().getEndPosition().toString()
                    )
            ));
        } catch (InvalidMoveException | IOException | DataAccessException ex) {
            throw new WebSocketException(ex.getMessage());
        }
    }

    private void leave(Connection connection, Leave message) throws WebSocketException {
        try {
            GameData game = db.getGame(message.getGameID());
            AuthData user = db.getAuth(message.getAuthString());

            // If the user is playing as both colors, this will remove them from both positions. Good?
            GameData updatedGame = new GameData(
                    game.gameID(),
                    Objects.equals(user.username(), game.whiteUsername()) ? null : game.whiteUsername(),
                    Objects.equals(user.username(), game.blackUsername()) ? null : game.blackUsername(),
                    game.gameName(),
                    game.game(),
                    false);
            db.updateGame(game.gameID(), updatedGame);
            gameRooms.get(message.getGameID()).remove(connection.visitorName);
            gameRooms.get(message.getGameID()).broadcast(connection.visitorName, new Notification(
                    connection.visitorName + " has left the game"
            ));
        } catch (IOException | DataAccessException ex) {
            throw new WebSocketException(ex.getMessage());
        }
    }

    private void resign(Connection connection, Resign message) throws WebSocketException {
        try {

        } catch (Exception ex) {
            throw new WebSocketException(ex.getMessage());
        }
    }
}
