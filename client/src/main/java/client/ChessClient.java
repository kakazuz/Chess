package client;

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
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "help" -> printPreloginHelp();
                case "quit", "exit" -> { return false; }
                case "login" -> doLogin();
                case "register" -> doRegister();
                default -> System.out.println("Unknown command. Type 'Help' for options.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private boolean doPostlogin() {
        System.out.print("[" + username + "] >>> ");
        String input = scanner.nextLine().trim();
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

    private void doLogin() {
    }

    private void doRegister() {
    }

    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        if (args.length >= 1) {
            serverUrl = args[0];
        }
        new ChessClient(serverUrl).run();
    }
}
