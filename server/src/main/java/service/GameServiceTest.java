package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;
    private UserService userService;

    private String authToken;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        RegisterLoginResult reg = userService.register(new RegisterRequest("player1", "pass123", "email@test.com"));
        authToken = reg.authToken();
    }

    @Test
    @DisplayName("Create Game - Positive")
    void createGamePositive() throws DataAccessException {
        CreateGameResult result = gameService.createGame(authToken, new CreateGameRequest("My Game"));
        assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("Create Game - Negative")
    void createGameNegativeBadAuth() {
        assertThrows(DataAccessException.class, () -> gameService.createGame("badToken", new CreateGameRequest("My Game")));
    }

    @Test
    @DisplayName("List Games - Positive")
    void listGamesPositive() throws DataAccessException {
        gameService.createGame(authToken, new CreateGameRequest("Game 1"));
        var games = gameService.listGames(authToken);
        assertFalse(games.isEmpty());
    }

    @Test
    @DisplayName("List Games - Negative")
    void listGamesNegativeBadAuth() {
        assertThrows(DataAccessException.class, () -> gameService.listGames("badToken"));
    }

    @Test
    @DisplayName("Join Game - Positive")
    void joinGamePositive() throws DataAccessException {
        int gameID = gameService.createGame(authToken, new CreateGameRequest("Join Test")).gameID();
        gameService.joinGame(authToken, new JoinGameRequest("WHITE", gameID));
    }

    @Test
    @DisplayName("Join Game - Negative")
    void joinGameNegativeAlreadyTaken() throws DataAccessException {
        int gameID = gameService.createGame(authToken, new CreateGameRequest("Join Test")).gameID();
        gameService.joinGame(authToken, new JoinGameRequest("WHITE", gameID));

        assertThrows(DataAccessException.class, () -> gameService.joinGame(authToken, new JoinGameRequest("WHITE", gameID)));
    }
}
