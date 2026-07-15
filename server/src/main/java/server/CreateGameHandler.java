package server;
import com.google.gson.Gson;
import io.javalin.http.Context;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;
import dataaccess.DataAccessException;

public class CreateGameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
            if(authToken == null || authToken.isBlank()) {
                ctx.status(401).result(gson.toJson(new ErrorMessage("unauthorized")));
                return;
            }
            CreateGameResult result = gameService.createGame(authToken, request);
            ctx.status(200).result(gson.toJson(result));

        } catch (DataAccessException e) {
            int status;
            if (e.getMessage().equals("bad request")) {
                status = 400;
            }
            if (e.getMessage().equals("unauthorized")) {
                status = 401;
            } else {
                status = 500;
            }
            ctx.status(status).result(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        } catch (Exception e) {
            ctx.status(500).result(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }
    private static class ErrorMessage {
        String message;
        ErrorMessage(String message) {
            this.message = message;
        }
    }
}
