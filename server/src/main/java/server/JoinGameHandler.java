package server;
import com.google.gson.Gson;
import io.javalin.http.Context;
import service.JoinGameRequest;
import service.GameService;
import dataaccess.DataAccessException;

public class JoinGameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            if(authToken == null || authToken.isBlank()) {
                ctx.status(401).result(gson.toJson(new ErrorMessage("unauthorized")));
                return;
            }
            gameService.joinGame(authToken, request);
            ctx.status(200).result("{}");

        } catch (DataAccessException e) {
            int status;
            if (e.getMessage().equals("bad request")) {
                status = 400;
            } else if (e.getMessage().equals("unauthorized")) {
                status = 401;
            } else if (e.getMessage().equals("already taken")) {
                status = 403;
            } else {
                status = 500;
            }
            ctx.status(status).result(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }
    private static class ErrorMessage {
        String message;
        ErrorMessage(String message) {
            this.message = message;
        }
    }
}
