package client;

import model.AuthData;

import java.util.Scanner;

public class ChessClient {

    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    private String authToken = null;
    private String username = null;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess");
        System.out.println("Type 'Help' to get started.\n");

        boolean running = true;
        while (running) {
            if (authToken == null) {
                running = doPrelogin();
            } else {
                running = doPostlogin();
            }
        }

        System.out.println("Thanks for playing!");
    }

    private boolean doPrelogin() {
        System.out.print("[LOGGED_OUT] >>> ");
        String input = scanner.nextLine().trim();
        String[] parts = parse(input);
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "help" -> printPreloginHelp();
                case "quit", "exit" -> { return false; }
                case "login" -> doLogin(parts);
                case "register" -> doRegister(parts);
                default -> System.out.println("Unknown command. Type 'Help' for options.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private boolean doPostlogin() {
        System.out.print("[LOGGED_IN] >>> ");
        String input = scanner.nextLine().trim();
        String[] parts = parse(input);
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "help" -> printPostloginHelp();
                case "quit", "exit" -> { return false; }
                case "list" -> doList();
                case "create" -> doCreate();
                case " join" -> doJoin();
                case " observe" -> doObserve();
                case " logout" -> doLogout();
                default -> System.out.println("Unknown command. Type 'Help' for options.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private void printPreloginHelp() {
        System.out.println("""
            register <USERNAME> <PASSWORD> <EMAIL>  - Create a new account
            login <USERNAME> <PASSWORD>             - Log in
            quit                                    - Exit the program
            help                                    - Show this help
            """);
    }

    private void printPostloginHelp() {
        System.out.println("""
            create <NAME>            - a game
            list                     - games
            join <ID> [WHITE][BACK]  - a game
            observe <ID>             - a game
            logout                   - when you are done
            quit                     - playing chess
            help                     - with possible commands
            """);
    }

    private void doLogin(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }

        String[] args = parts[1].split("\\s+");
        if (args.length < 2) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }

        String username = args[0];
        String password = args[1];

        try {
            AuthData auth = server.login(username, password);
            this.authToken = auth.authToken();
            this.username = auth.username();
            System.out.println("Logged in as " + username);
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void doRegister() {
    }

    private String[] parse(String input) {
        return input.trim().split("\\s+", 2);
    }

    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        if (args.length >= 1) {
            serverUrl = args[0];
        }
        new ChessClient(serverUrl).run();
    }
}
