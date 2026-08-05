package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;

@ClientEndpoint
public class WebSocketFacade extends Endpoint {

    private Session session;
    private final Gson gson = new Gson();
    private final ServerMessageHandler messageHandler;

    public WebSocketFacade(String serverUrl, ServerMessageHandler messageHandler) throws Exception {
        this.messageHandler = messageHandler;

        String wsUrl = serverUrl.replace("http", "ws") + "/ws";

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(wsUrl));
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                messageHandler.onMessage(serverMessage);
            }
        });
    }

    public void connect(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        send(command);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        send(command);
    }

    public void leave(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        send(command);
    }

    public void resign(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        send(command);
    }

    private void send(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void close() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    public interface ServerMessageHandler {
        void onMessage(ServerMessage message);
    }
}