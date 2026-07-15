package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.ClearService;
import dataaccess.DataAccessException;

public class ClearHandler {

    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }
    public void handle(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200).result("{}");

        } catch (DataAccessException e) {
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
