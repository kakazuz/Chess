package client;

import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ServerFacade(int port) {
        this("http://localhost:" + port);
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var request = Map.of("username", username, "password", password, "email", email);
        return makeRequest("POST", "/user", request, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws Exception {
        var request = Map.of("username", username, "password", password);
        return makeRequest("POST", "/session", request, AuthData.class, null);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, null, authToken);
    }


    public int createGame(String authToken, String gameName) throws Exception {
        var request = Map.of("gameName", gameName);
        var response = makeRequest("POST", "/game", request, Map.class, authToken);
        return ((Double) response.get("gameID")).intValue();
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        Map response = makeRequest("GET", "/game", null, Map.class, authToken);
        var gamesList = (java.util.List<Map>) response.get("games");
        var result = new java.util.ArrayList<GameData>();
        for (Map gameMap : gamesList) {
            GameData game = gson.fromJson(gson.toJson(gameMap), GameData.class);
            result.add(game);
        }
        return result;
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        var request = Map.of("gameID", gameID, "playerColor", playerColor);
        makeRequest("PUT", "/game", request, null, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request,
                              Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream outputStream = http.getOutputStream()) {
                outputStream.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private static void throwIfNotSuccessful(HttpURLConnection http) throws IOException, Exception {
        var status = http.getResponseCode();
        if (status / 100 != 2) {
            throw new Exception("HTTP Error: " + status);
        }
    }
}
