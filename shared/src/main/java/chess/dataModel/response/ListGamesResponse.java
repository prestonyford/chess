package chess.dataModel.response;

import chess.dataModel.GameData;

public record ListGamesResponse(GameData[] games) {
}
