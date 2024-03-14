import chess.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        new Repl(serverUrl).run();
    }
}