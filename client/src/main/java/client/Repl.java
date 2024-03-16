package client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static client.ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) throws MalformedURLException, URISyntaxException {
        client = new ChessClient(serverUrl);
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
}