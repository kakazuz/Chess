package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @DisplayName("createAuth - Positive")
    void createAuthPositive() throws DataAccessException {
        userDAO.createUser(new UserData("alice", "password123", "alice@email.com"));
        AuthData auth = new AuthData("token-123", "alice");
        authDAO.createAuth(auth);

        AuthData retrieved = authDAO.getAuth("token-123");
        assertEquals("token-123", retrieved.authToken());
        assertEquals("alice", retrieved.username());
    }

    @Test
    @DisplayName("createAuth - Negative")
    void createAuthNegativeDuplicate() throws DataAccessException {
        userDAO.createUser(new UserData("alice", "password123", "alice@email.com"));
        AuthData auth = new AuthData("token-123", "alice");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(auth);
        });
    }

    @Test
    @DisplayName("getAuth - Positive")
    void getAuthPositive() throws DataAccessException {
        userDAO.createUser(new UserData("bob", "secret", "bob@email.com"));
        AuthData auth = new AuthData("token-abc", "bob");
        authDAO.createAuth(auth);

        AuthData retrieved = authDAO.getAuth("token-abc");
        assertEquals("token-abc", retrieved.authToken());
        assertEquals("bob", retrieved.username());
    }

    @Test
    @DisplayName("getAuth - Negative")
    void getAuthNegativeNotFound() {
        assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth("non-existent-token");
        });
    }

    @Test
    @DisplayName("deleteAuth - Positive")
    void deleteAuthPositive() throws DataAccessException {
        userDAO.createUser(new UserData("charlie", "pass", "charlie@email.com"));
        AuthData auth = new AuthData("token-to-delete", "charlie");
        authDAO.createAuth(auth);

        authDAO.deleteAuth("token-to-delete");

        assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth("token-to-delete");
        });
    }

    @Test
    @DisplayName("deleteAuth - Negative")
    void deleteAuthNegativeNotFound() {
        assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth("fake-token-999");
        });
    }

    @Test
    @DisplayName("clear - Positive")
    void clearPositive() throws DataAccessException {
        userDAO.createUser(new UserData("tempuser", "pass", "temp@email.com"));
        authDAO.createAuth(new AuthData("temp-token", "tempuser"));
        authDAO.clear();

        assertThrows(DataAccessException.class, () -> {
            authDAO.getAuth("temp-token");
        });
    }
}
