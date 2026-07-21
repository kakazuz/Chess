package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    private SQLGameDAO gameDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        userDAO.clear();
        gameDAO.clear();
    }

    @Test
    @DisplayName("createGame - Positive")
    void createGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");

        assertTrue(gameID > 0);

        GameData game = gameDAO.getGame(gameID);
        assertEquals(gameID, game.gameID());
        assertEquals("Test Game", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertNotNull(game.game());
    }

    @Test
    @DisplayName("createGame - Negative")
    void createGameNegativeBadName() {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null);
        });
    }

    @Test
    @DisplayName("getGame - Positive")
    void getGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("My Game");
        GameData game = gameDAO.getGame(gameID);

        assertEquals(gameID, game.gameID());
        assertEquals("My Game", game.gameName());
        assertNotNull(game.game());
    }

    @Test
    @DisplayName("getGame - Negative")
    void getGameNegativeNotFound() {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.getGame(99999);
        });
    }

    @Test
    @DisplayName("listGames - Positive")
    void listGamesPositive() throws DataAccessException {
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("listGames - Positive")
    void listGamesPositiveEmpty() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    @DisplayName("updateGame - Positive")
    void updateGamePositive() throws DataAccessException {
        userDAO.createUser(new UserData("alice", "pass", "alice@email.com"));

        int gameID = gameDAO.createGame("Original Name");

        GameData original = gameDAO.getGame(gameID);
        GameData updated = new GameData(
                gameID,
                "alice",
                null,
                "Original Name",
                original.game()
        );

        gameDAO.updateGame(updated);

        GameData retrieved = gameDAO.getGame(gameID);
        assertEquals("alice", retrieved.whiteUsername());
        assertNull(retrieved.blackUsername());
    }

    @Test
    @DisplayName("updateGame - Negative")
    void updateGameNegativeNotFound() {
        GameData fakeGame = new GameData(
                99999,
                "nobody",
                null,
                "Fake",
                new ChessGame()
        );

        assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(fakeGame);
        });
    }

    @Test
    @DisplayName("clear - Positive")
    void clearPositive() throws DataAccessException {
        int gameID = gameDAO.createGame("Temp Game");
        gameDAO.clear();

        assertThrows(DataAccessException.class, () -> {
            gameDAO.getGame(gameID);
        });
    }
}
