package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private final GameService gameService;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();


    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, WsContext>> gameConnections = new ConcurrentHashMap<>();

    public WebSocketHandler(GameService gameService, AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = gameService;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void onConnect(WsContext ctx) {
        System.out.println("Client connected: " + ctx.sessionId());
    }

    public void onMessage(WsContext ctx, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            System.out.println("Received command: " + command.getCommandType());

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(ctx, command);
                case MAKE_MOVE -> handleMakeMove(ctx, command);
                case LEAVE -> handleLeave(ctx, command);
                case RESIGN -> handleResign(ctx, command);
            }
        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    public void onClose(WsContext ctx) {
        System.out.println("Client disconnected: " + ctx.sessionId());
    }

    private void handleConnect(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            String username = auth.username();

            GameData gameData = gameDAO.getGame(command.getGameID());

            gameConnections.putIfAbsent(command.getGameID(), new ConcurrentHashMap<>());
            gameConnections.get(command.getGameID()).put(ctx.sessionId(), ctx);

            ctx.attribute("username", username);
            ctx.attribute("gameID", command.getGameID());

            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(gameData.game());
            ctx.send(gson.toJson(loadGame));

            String notificationText;
            if (username.equals(gameData.whiteUsername())) {
                notificationText = username + " joined as white";
            } else if (username.equals(gameData.blackUsername())) {
                notificationText = username + " joined as black";
            } else {
                notificationText = username + " joined as an observer";
            }

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(notificationText);
            broadcast(command.getGameID(), notification, ctx);

        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private void handleMakeMove(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            String username = auth.username();
            int gameID = command.getGameID();
            ChessMove move = command.getMove();

            if (move == null) {
                sendError(ctx, "Error: No move provided");
                return;
            }

            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();

            if (game.getTeamTurn() == null) {
                sendError(ctx, "Error: Game is already over");
                return;
            }

            ChessGame.TeamColor playerColor = null;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                sendError(ctx, "Error: You are not a player in this game");
                return;
            }

            if (game.getTeamTurn() != playerColor) {
                sendError(ctx, "Error: It is not your turn");
                return;
            }

            game.makeMove(move);

            GameData updated = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            );
            gameDAO.updateGame(updated);

            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game);
            broadcast(gameID, loadGame, null);

            String moveDescription = username + " moved";
            ServerMessage moveNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            moveNotification.setMessage(moveDescription);
            broadcast(gameID, moveNotification, ctx);

            ChessGame.TeamColor opponent = (playerColor == ChessGame.TeamColor.WHITE)
                    ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;

            if (game.isInCheckmate(opponent)) {
                ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notif.setMessage(username + " checkmated their opponent");
                broadcast(gameID, notif, null);
            } else if (game.isInCheck(opponent)) {
                ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notif.setMessage("Check!");
                broadcast(gameID, notif, null);
            } else if (game.isInStalemate(opponent)) {
                ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notif.setMessage("Stalemate!");
                broadcast(gameID, notif, null);
            }

        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private void handleLeave(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            String username = auth.username();
            int gameID = command.getGameID();

            var connections = gameConnections.get(gameID);
            if (connections != null) {
                connections.remove(ctx.sessionId());
                if (connections.isEmpty()) {
                    gameConnections.remove(gameID);
                }
            }

            GameData gameData = gameDAO.getGame(gameID);
            String white = gameData.whiteUsername();
            String black = gameData.blackUsername();

            if (username.equals(white) || username.equals(black)) {
                String newWhite = username.equals(white) ? null : white;
                String newBlack = username.equals(black) ? null : black;

                GameData updated = new GameData(
                        gameData.gameID(),
                        newWhite,
                        newBlack,
                        gameData.gameName(),
                        gameData.game()
                );
                gameDAO.updateGame(updated);
            }

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(username + " left the game");
            broadcast(gameID, notification, ctx);

        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private void handleResign(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            String username = auth.username();
            int gameID = command.getGameID();

            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();

            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                sendError(ctx, "Error: Only players can resign");
                return;
            }

            if (game.getTeamTurn() == null) {
                sendError(ctx, "Error: Game is already over");
                return;
            }

            game.setTeamTurn(null);

            GameData updated = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            );
            gameDAO.updateGame(updated);

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(username + " resigned");
            broadcast(gameID, notification, null);

        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private void sendError(WsContext ctx, String errorMessage) {
        ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        error.setErrorMessage(errorMessage);
        ctx.send(gson.toJson(error));
    }

    private void broadcast(int gameID, ServerMessage message, WsContext exclude) {
        var connections = gameConnections.get(gameID);
        if (connections == null) return;

        String json = gson.toJson(message);
        for (WsContext connection : connections.values()) {
            if (exclude == null || !connection.sessionId().equals(exclude.sessionId())) {
                connection.send(json);
            }
        }
    }
}
