package client;

import chess.*;
import ui.EscapeSequences;
import model.AuthData;
import model.GameData;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements WebSocketFacade.ServerMessageHandler{

    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    private String authToken = null;
    private String username = null;
    private java.util.List<GameData> games = new java.util.ArrayList<>();

    private final String serverUrl;
    private WebSocketFacade webSocket;
    private int currentGameID = -1;
    private ChessGame currentGame = null;
    private boolean inGameplay = false;
    private boolean whitePerspective = true;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess");
        System.out.println("Type 'Help' to get started.\n");

        boolean running = true;
        while (running) {
            if (authToken == null) {
                running = preLogin();
            } else {
                running = postLogin();
            }
        }

        System.out.println("Thanks for playing!");
    }

    @Override
    public void onMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                currentGame = message.getGame();
                drawBoard(currentGame, whitePerspective);
            }
            case NOTIFICATION -> {
                System.out.println("\n*** " + message.getMessage() + " ***");
                System.out.print("[" + username + "] >>> ");
            }
            case ERROR -> {
                System.out.println("\nError: " + message.getErrorMessage());
                System.out.print("[" + username + "] >>> ");
            }
        }
    }

    private boolean preLogin() {
        System.out.print("[LOGGED_OUT] >>> ");
        String input = scanner.nextLine().trim();
        String[] parts = parse(input);
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "help" -> printPreloginHelp();
                case "quit", "exit" -> { return false; }
                case "login" -> login(parts);
                case "register" -> register(parts);
                default -> System.out.println("Unknown command. Type 'Help' for options.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private boolean postLogin() {
        System.out.print("[LOGGED_IN] >>> ");
        String input = scanner.nextLine().trim();
        String[] parts = parse(input);
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "help" -> printPostloginHelp();
                case "quit", "exit" -> { return false; }
                case "list" -> list();
                case "create" -> create(parts);
                case "join" -> join(parts);
                case "observe" -> observe(parts);
                case "logout" -> logout();
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

    private void login(String[] parts) {
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

    private void register(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }

        String[] args = parts[1].split("\\s+");
        if (args.length < 3) {
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }

        String username = args[0];
        String password = args[1];
        String email = args[2];

        try {
            AuthData auth = server.register(username, password, email);
            this.authToken = auth.authToken();
            this.username = auth.username();
            System.out.println("Registered and logged in as " + username);
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void list() {
        try {
            games = new java.util.ArrayList<>(server.listGames(authToken));

            if (games.isEmpty()) {
                System.out.println("No games available.");
                return;
            }

            System.out.println("Current games:");
            for (int i = 0; i < games.size(); i++) {
                GameData g = games.get(i);
                String white = g.whiteUsername() != null ? g.whiteUsername() : "(waiting)";
                String black = g.blackUsername() != null ? g.blackUsername() : "(waiting)";
                System.out.printf("%d. %s  |  White: %s  |  Black: %s%n",
                        i + 1, g.gameName(), white, black);
            }
        } catch (Exception e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void create(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: create <NAME>");
            return;
        }
        String gameName = parts[1].trim();

        try {
            server.createGame(authToken, gameName);
            System.out.println("Created game " + gameName);
        } catch (Exception e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private void join(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: join <ID> [WHITE][BLACK]");
            return;
        }

        String[] args = parts[1].split("\\s+");
        if (args.length < 2) {
            System.out.println("Usage: join <ID> [WHITE][BLACK]");
            return;
        }

        try {
            int index = Integer.parseInt(args[0]) - 1;
            String color = args[1].toUpperCase();

            if (index < 0 || index >= games.size()) {
                System.out.println("Invalid game number.");
                return;
            }
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Color must be WHITE or BLACK.");
                return;
            }

            GameData selected = games.get(index);

            server.joinGame(authToken, selected.gameID(), color);

            this.currentGameID = selected.gameID();
            this.whitePerspective = color.equals("WHITE");
            this.inGameplay = true;

            System.out.println("Joined game \"" + selected.gameName() + "\" as " + color);

            this.webSocket = new WebSocketFacade(serverUrl, this);
            this.webSocket.connect(authToken, currentGameID);

            doGameplay();

            inGameplay = false;
            currentGame = null;
            currentGameID = -1;

        } catch (NumberFormatException e) {
            System.out.println("Game number must be an integer.");
        } catch (Exception e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void observe(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: observe <ID>");
            return;
        }

        try {
            int index = Integer.parseInt(parts[1].trim()) - 1;

            if (index < 0 || index >= games.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData selected = games.get(index);
            System.out.println("Observing game \"" + selected.gameName() + "\"");
            ChessGame gameToDraw = selected.game() != null ? selected.game() : new ChessGame();
            drawBoard(gameToDraw, true);
            this.currentGameID = selected.gameID();
            this.whitePerspective = true;
            this.inGameplay = true;

            this.webSocket = new WebSocketFacade(serverUrl, this);
            this.webSocket.connect(authToken, currentGameID);

            doGameplay();

        } catch (NumberFormatException e) {
            System.out.println("Game number must be an integer.");
        } catch (Exception e) {
            System.out.println("Failed to observe game: " + e.getMessage());
        }
    }

    private void logout() {
        try {
            server.logout(authToken);
            authToken = null;
            username = null;
            games.clear();
            System.out.println("Logged out.");
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private String[] parse(String input) {
        return input.trim().split("\\s+", 2);
    }

    private void drawBoard(ChessGame game, boolean whitePerspective) {
        ChessBoard board = game.getBoard();

        final String light = "\u001B[48;5;220m";   // Gold
        final String dark  = "\u001B[48;5;124m";   // Cardinal Red
        final String reset = RESET_BG_COLOR + RESET_TEXT_COLOR;

        System.out.println();

        String headers = whitePerspective
                ? "    a  b  c  d  e  f  g  h"
                : "    h  g  f  e  d  c  b  a";
        System.out.println(headers);

        for (int row = 0; row < 8; row++) {
            int displayRow = whitePerspective ? 8 - row : row + 1;
            System.out.print(" " + displayRow + " ");

            for (int col = 0; col < 8; col++) {
                int actualRow = whitePerspective ? 8 - row : row + 1;
                int actualCol = whitePerspective ? col + 1 : 8 - col;

                ChessPosition pos = new ChessPosition(actualRow, actualCol);
                ChessPiece piece = board.getPiece(pos);

                boolean isLight = (actualRow + actualCol) % 2 != 0;
                String bg = isLight ? light : dark;

                String pieceStr = EMPTY;
                if (piece != null) {
                    pieceStr = getPieceChar(piece);
                }

                System.out.print(bg + pieceStr + reset);
            }
            System.out.println(" " + displayRow);
        }

        System.out.println(headers);
        System.out.println();
    }

    private void doGameplay() {
        System.out.println("Entered gameplay. Type 'help' for commands.");
        while (inGameplay) {
            System.out.print("[GAME] >>> ");
            String input = scanner.nextLine().trim();
            String[] parts = parse(input);
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "help" -> printGameplayHelp();
                    case "redraw" -> {
                        if (currentGame != null) drawBoard(currentGame, whitePerspective);
                    }
                    case "leave" -> {
                        try {
                            if (webSocket != null) {
                                webSocket.leave(authToken, currentGameID);
                                webSocket.close();
                                webSocket = null;
                            }
                        } catch (Exception e) {
                            System.out.println("Leave error: " + e.getMessage());
                        }
                        inGameplay = false;
                        System.out.println("Left gameplay.");
                    }
                    case "resign" -> {
                        System.out.print("Are you sure you want to resign? (yes/no): ");
                        String answer = scanner.nextLine().trim().toLowerCase();
                        if (answer.equals("yes") || answer.equals("y")) {
                            if (webSocket == null) {
                                System.out.println("Not connected.");
                            } else {
                                webSocket.resign(authToken, currentGameID);
                            }
                        } else {
                            System.out.println("Resign cancelled.");
                        }
                    }
                    case "move" -> doMakeMove(parts);
                    case "highlight" -> doHighlight(parts);
                    default -> System.out.println("Unknown command. Type 'help'.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printGameplayHelp() {
        System.out.println("""
        redraw          - Redraw the board
        leave           - Leave the game
        resign          - Resign the game
        move            - Make a move (e.g. move e2 e4)
        highlight       - Highlight legal moves for a piece
        help            - Show this help
        """);
    }

    private void doMakeMove(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: move <from> <to>   e.g. move e2 e4");
            return;
        }

        if (webSocket == null) {
            System.out.println("webSocket is null");
            return;
        }

        String[] args = parts[1].split("\\s+");
        if (args.length < 2) {
            System.out.println("Usage: move <from> <to>   e.g. move e2 e4");
            return;
        }

        try {
            ChessPosition start = parsePosition(args[0]);
            ChessPosition end = parsePosition(args[1]);

            ChessPiece.PieceType promotion = null;
            if (args.length >= 3) {
                promotion = switch (args[2].toLowerCase()) {
                    case "q" -> ChessPiece.PieceType.QUEEN;
                    case "r" -> ChessPiece.PieceType.ROOK;
                    case "b" -> ChessPiece.PieceType.BISHOP;
                    case "n" -> ChessPiece.PieceType.KNIGHT;
                    default -> null;
                };
            }

            ChessMove move = new ChessMove(start, end, promotion);
            webSocket.makeMove(authToken, currentGameID, move);

        } catch (Exception e) {
            System.out.println("Invalid move: " + e.getMessage());
        }
    }

    private ChessPosition parsePosition(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Position must be like e2");
        }

        char colChar = pos.toLowerCase().charAt(0);
        char rowChar = pos.charAt(1);

        int col = colChar - 'a' + 1;
        int row = rowChar - '0';

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Invalid square: " + pos);
        }

        return new ChessPosition(row, col);
    }

    private void doHighlight(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: highlight <square>   e.g. highlight e2");
            return;
        }

        if (currentGame == null) {
            System.out.println("No game loaded.");
            return;
        }

        try {
            ChessPosition position = parsePosition(parts[1].trim());
            ChessPiece piece = currentGame.getBoard().getPiece(position);

            if (piece == null) {
                System.out.println("No piece on that square.");
                return;
            }

            Collection<ChessMove> legalMoves = currentGame.validMoves(position);
            if (legalMoves == null) {
                legalMoves = java.util.List.of();
            }

            drawBoardHighlighted(currentGame, whitePerspective, position, legalMoves);

            System.out.println("Socket open after highlight? " +
                    (webSocket != null));

        } catch (Exception e) {
            System.out.println("Highlight failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawBoardHighlighted(ChessGame game, boolean whitePerspective,
                                      ChessPosition selected, Collection<ChessMove> legalMoves) {
        ChessBoard board = game.getBoard();

        final String light = "\u001B[48;5;220m";
        final String dark  = "\u001B[48;5;124m";
        final String highlight = "\u001B[48;5;34m";
        final String selectedBg = "\u001B[48;5;27m";
        final String reset = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

        java.util.Set<String> highlightSquares = new java.util.HashSet<>();
        if (legalMoves != null) {
            for (ChessMove move : legalMoves) {
                ChessPosition end = move.getEndPosition();
                highlightSquares.add(end.getRow() + "," + end.getColumn());
            }
        }
        String selectedKey = selected.getRow() + "," + selected.getColumn();

        System.out.println();
        String headers = whitePerspective
                ? "    a  b  c  d  e  f  g  h"
                : "    h  g  f  e  d  c  b  a";
        System.out.println(headers);

        for (int row = 0; row < 8; row++) {
            int displayRow = whitePerspective ? 8 - row : row + 1;
            System.out.print(" " + displayRow + " ");

            for (int col = 0; col < 8; col++) {
                int actualRow = whitePerspective ? 8 - row : row + 1;
                int actualCol = whitePerspective ? col + 1 : 8 - col;

                ChessPosition pos = new ChessPosition(actualRow, actualCol);
                ChessPiece piece = board.getPiece(pos);

                String key = actualRow + "," + actualCol;
                boolean isLight = (actualRow + actualCol) % 2 != 0;

                String bg;
                if (key.equals(selectedKey)) {
                    bg = selectedBg;
                } else if (highlightSquares.contains(key)) {
                    bg = highlight;
                } else {
                    bg = isLight ? light : dark;
                }

                String pieceStr = EscapeSequences.EMPTY;
                if (piece != null) {
                    pieceStr = getPieceChar(piece);
                }

                System.out.print(bg + pieceStr + reset);
            }
            System.out.println(" " + displayRow);
        }

        System.out.println(headers);
        System.out.println();
    }

    private String getPieceChar(ChessPiece piece) {
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        String pieceSymbol = switch (piece.getPieceType()) {
            case KING   -> isWhite ? WHITE_KING   : BLACK_KING;
            case QUEEN  -> isWhite ? WHITE_QUEEN  : BLACK_QUEEN;
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK   -> isWhite ? WHITE_ROOK   : BLACK_ROOK;
            case PAWN   -> isWhite ? WHITE_PAWN   : BLACK_PAWN;
        };

        return SET_TEXT_COLOR_BLACK + pieceSymbol + RESET_TEXT_COLOR;
    }

}
