package chess.dataModel.response;

import chess.dataModel.GameData;

import java.util.Arrays;
import java.util.stream.Collectors;

public record ListGamesResponse(GameData[] games) {
    @Override
    public String toString() {
        return Arrays.stream(games).map(GameData::toString).collect(Collectors.joining("\n======================\n"));
    }
}
