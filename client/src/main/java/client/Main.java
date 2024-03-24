package client;

public class Main {
    public static void main(String[] args) throws Exception {
        String domainName = "localhost:8080";
        if (args.length == 1) {
            domainName = args[0];
        }
        new Repl(domainName).run();
    }
}