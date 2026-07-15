package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.LoginRequest;
import service.RegisterLoginResult;
import service.UserService;
import dataaccess.DataAccessException;

public class LoginHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }
    public void handle(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            RegisterLoginResult result = userService.login(request);
            ctx.status(200).result(gson.toJson(result));
        } catch (DataAccessException e) {
            int status;
            if (e.getMessage().equals("bad request")) {
                status = 400;
            } else if (e.getMessage().equals("unauthorized")) {
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

