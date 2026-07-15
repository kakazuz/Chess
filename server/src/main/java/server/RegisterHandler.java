package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;
import dataaccess.DataAccessException;


public class RegisterHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handle(Context ctx) {
        try {
            // Convert JSON body to RegisterRequest
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);

            // Call the service
            RegisterResult result = userService.register(request);

            // Send success response
            ctx.status(200).json(gson.toJson(result));

        } catch (DataAccessException e) {
            if (e.getMessage().equals("bad request")) {
                ctx.status(400);
            } else if (e.getMessage().equals("already taken")) {
                ctx.status(403);
            } else {
                ctx.status(500);
            }
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private static class ErrorMessage {
        String message;
        ErrorMessage(String message) {
            this.message = message;
        }
    }
}
