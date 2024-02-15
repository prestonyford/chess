package chess.dataModel.request;

public record CreateGameRequest(String authToken, String gameName)  {
}
