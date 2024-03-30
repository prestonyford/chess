package client;

import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static client.ui.EscapeSequences.*;

public class Repl implements MessageHandler.Whole<String> {
    private final ChessClient client;

    public Repl(String domainName) throws IOException, URISyntaxException, DeploymentException {
        client = new ChessClient(domainName, this);
    }

    public void run() {
        System.out.println("♕ Welcome to CS 240 Chess Client ♕" + SET_TEXT_COLOR_BLUE);
        System.out.println(client.help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            result = client.eval(line);
            System.out.print(SET_TEXT_COLOR_BLUE + result);
            System.out.print((result.isEmpty()) ? "" : '\n');
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void onMessage(String s) {
        System.out.println("Received: " + s);
    }

//    @Override
//    public void onServerMessage(ServerMessage serverMessage) {
//        System.out.println(serverMessage);
//    }
}
