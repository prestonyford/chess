package chess.dataModel;

import chess.ChessGame;

public record GameData(
        Integer gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game
) {
    @Override
    public String toString() {
        return String.format(
                "ID: %d\nName: %s\nWhite: %s\nBlack: %s",
                gameID,
                gameName,
                whiteUsername,
                blackUsername
        );
    }
}
