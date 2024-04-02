package client;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static client.ui.EscapeSequences.*;

public class Repl implements ClientOutput {
    private final ChessClient client;

    public Repl(String domainName) {
        client = new ChessClient(domainName, this);
    }

    public void run() {
        System.out.println("♕ Welcome to CS 240 Chess Client ♕" + SET_TEXT_COLOR_BLUE);
        client.eval("help");
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while (!line.equals("quit")) {
            line = scanner.nextLine();
            System.out.print(SET_TEXT_COLOR_BLUE);
            client.eval(line);
        }
        System.out.println();
    }

    public void prompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void output(String message) {
        System.out.print(message);
    }
}
