package server;
import com.google.gson.Gson;
import io.javalin.http.Context;
import model.GameData;
import service.GameDataForList;
import service.GameService;
import dataaccess.DataAccessException;
import java.util.Collection;

public class ListGamesHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }
    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if(authToken == null || authToken.isBlank()) {
                ctx.status(401).result(gson.toJson(new ErrorMessage("unauthorized")));
                return;
            }
            Collection<GameDataForList> result = gameService.listGames(authToken);
            ctx.status(200).result(gson.toJson(result));

        } catch (DataAccessException e) {
            int status;
            if (e.getMessage().equals("unauthorized")) {
                status = 401;
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

