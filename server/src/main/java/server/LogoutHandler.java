package server;
import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import dataaccess.DataAccessException;

public class LogoutHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }
    public void handle(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if(authToken == null || authToken.isBlank()) {
                ctx.status(401).result(gson.toJson(new ErrorMessage("unauthorized")));
                return;
            }
            userService.logout(authToken);
            ctx.status(200).result("{}");

        } catch (DataAccessException e) {
            int status;
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
