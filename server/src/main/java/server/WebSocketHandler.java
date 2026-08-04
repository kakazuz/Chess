package server;

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
    }

    private void handleLeave(WsContext ctx, UserGameCommand command) {
    }

    private void handleResign(WsContext ctx, UserGameCommand command) {
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
