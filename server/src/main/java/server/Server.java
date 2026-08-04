package server;

import dataaccess.*;
import io.javalin.*;
import service.ClearService;
import service.UserService;
import service.GameService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        UserDAO userDAO = null;
        AuthDAO authDAO = null;
        GameDAO gameDAO = null;
        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to initialize database(config)", e);
        }

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin.post("/user", registerHandler::handle);
        javalin.post("/session", loginHandler::handle);
        javalin.delete("/session", logoutHandler::handle);
        javalin.post("/game", createGameHandler::handle);
        javalin.get("/game", listGamesHandler::handle);
        javalin.put("/game", joinGameHandler::handle);
        javalin.delete("/db", clearHandler::handle);

        WebSocketHandler webSocketHandler = new WebSocketHandler(gameService, authDAO, gameDAO);

        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler::onConnect);
            ws.onMessage(ctx -> webSocketHandler.onMessage(ctx, ctx.message()));
            ws.onClose(webSocketHandler::onClose);
            ws.onError(ctx -> System.out.println("WebSocket error: " + ctx.error()));
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
