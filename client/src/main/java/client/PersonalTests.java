package client;

import chess.dataModel.request.CreateGameRequest;
import chess.dataModel.request.JoinGameRequest;
import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.CreateGameResponse;
import client.http.HttpCommunicator;
import client.webSocket.WebSocketCommunicator;

import javax.websocket.MessageHandler;

public class PersonalTests implements MessageHandler.Whole<String> {

    public static void main(String[] args) throws Exception {
        String domainName = "localhost:8080";
        ServerFacade serverFacade = new ServerFacade(domainName, new PersonalTests());
        serverFacade.clearDB();
        serverFacade.register(new RegisterRequest("py", "py", "py"));
        CreateGameResponse createGameResponse = serverFacade.createGame(new CreateGameRequest("test"));
        serverFacade.joinGame(new JoinGameRequest("white", createGameResponse.gameID()));

        ServerFacade serverFacade2 = new ServerFacade(domainName, new PersonalTests());
        serverFacade2.register(new RegisterRequest("py2", "py2", "py2"));
        serverFacade2.joinGame(new JoinGameRequest("black", createGameResponse.gameID()));

        Thread.sleep(5000);
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s + '\n');
    }
}
