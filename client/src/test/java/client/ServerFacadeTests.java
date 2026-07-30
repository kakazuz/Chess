package client;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        try {
            server = new Server();
            var port = server.run(0);
            System.out.println("Started test HTTP server on " + port);
            facade = new ServerFacade(port);
            System.out.println("ServerFacade created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to start server or create facade: " + e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

//    @BeforeEach
//    void clearDatabase() {
//        try {
//            facade.clear(); // only works if you have a clear method on ServerFacade
//        } catch (Exception e) {
//            // ignore if clear isn't implemented or fails
//        }
//    }

    // ==================== REGISTER ====================
    @Test
    void registerPositive() throws Exception {
        String username = "user" + System.currentTimeMillis();
        AuthData auth = facade.register(username, "password", username + "@email.com");

        assertNotNull(auth);
        assertEquals(username, auth.username());
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void registerNegativeDuplicate() throws Exception {
        String username = "dup" + System.currentTimeMillis();
        facade.register(username, "password", username + "@email.com");

        assertThrows(Exception.class, () -> {
            facade.register(username, "password", username + "@email.com");
        });
    }

    // ==================== LOGIN ====================
    @Test
    void loginPositive() throws Exception {
        String username = "login" + System.currentTimeMillis();
        facade.register(username, "password", username + "@email.com");

        AuthData auth = facade.login(username, "password");
        assertNotNull(auth);
        assertEquals(username, auth.username());
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void loginNegativeWrongPassword() throws Exception {
        String username = "badlogin" + System.currentTimeMillis();
        facade.register(username, "correct", username + "@email.com");

        assertThrows(Exception.class, () -> {
            facade.login(username, "wrongpassword");
        });
    }

    // ==================== LOGOUT ====================
    @Test
    void logoutPositive() throws Exception {
        String username = "logout" + System.currentTimeMillis();
        AuthData auth = facade.register(username, "password", username + "@email.com");

        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    void logoutNegativeInvalidToken() {
        assertThrows(Exception.class, () -> {
            facade.logout("invalid-token-12345");
        });
    }

    // ==================== CREATE GAME ====================
    @Test
    void createGamePositive() throws Exception {
        String username = "create" + System.currentTimeMillis();
        AuthData auth = facade.register(username, "password", username + "@email.com");

        int gameID = facade.createGame(auth.authToken(), "Test Game");
        assertTrue(gameID > 0);
    }

    @Test
    void createGameNegativeBadAuth() {
        assertThrows(Exception.class, () -> {
            facade.createGame("bad-token", "Should Fail");
        });
    }

    // ==================== LIST GAMES ====================
    @Test
    void listGamesPositive() throws Exception {
        String username = "list" + System.currentTimeMillis();
        AuthData auth = facade.register(username, "password", username + "@email.com");

        facade.createGame(auth.authToken(), "Game A");
        facade.createGame(auth.authToken(), "Game B");

        Collection<GameData> games = facade.listGames(auth.authToken());
        assertTrue(games.size() >= 2);
    }

    @Test
    void listGamesNegativeBadAuth() {
        assertThrows(Exception.class, () -> {
            facade.listGames("invalid-token");
        });
    }

    // ==================== JOIN GAME ====================
    @Test
    void joinGamePositive() throws Exception {
        String username = "join" + System.currentTimeMillis();
        AuthData auth = facade.register(username, "password", username + "@email.com");

        int gameID = facade.createGame(auth.authToken(), "Joinable Game");

        assertDoesNotThrow(() -> {
            facade.joinGame(auth.authToken(), gameID, "WHITE");
        });
    }

    @Test
    void joinGameNegativeBadAuth() throws Exception {
        String username = "join2" + System.currentTimeMillis();
        AuthData auth = facade.register(username, "password", username + "@email.com");

        int gameID = facade.createGame(auth.authToken(), "Another Game");

        assertThrows(Exception.class, () -> {
            facade.joinGame("bad-token", gameID, "BLACK");
        });
    }
}
