package webSocketMessages.serverMessages;

import chess.ChessGame;
import chess.dataModel.GameData;

public class LoadGame extends ServerMessage {
    private final GameData game;

    public LoadGame(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }
}
