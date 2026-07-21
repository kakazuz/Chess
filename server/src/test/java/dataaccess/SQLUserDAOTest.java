package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    @DisplayName("createUser - Positive")
    void createUserPositive() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@email.com");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("alice");
        assertEquals("alice", retrieved.username());
        assertEquals("alice@email.com", retrieved.email());
        assertNotNull(retrieved.password());
        assertFalse(retrieved.password().isBlank());
    }

    @Test
    @DisplayName("createUser - Negative")
    void createUserNegativeDuplicate() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@email.com");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(user);
        });
    }

    @Test
    @DisplayName("getUser - Positive")
    void getUserPositive() throws DataAccessException {
        UserData user = new UserData("bob", "secret", "bob@email.com");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("bob");
        assertEquals("bob", retrieved.username());
        assertEquals("bob@email.com", retrieved.email());
    }

    @Test
    @DisplayName("getUser - Negative")
    void getUserNegativeNotFound() {
        assertThrows(DataAccessException.class, () -> {
            userDAO.getUser("nonexistent");
        });
    }

    @Test
    @DisplayName("clear - Positive")
    void clearPositive() throws DataAccessException {
        userDAO.createUser(new UserData("temp", "pass", "temp@email.com"));
        userDAO.clear();

        assertThrows(DataAccessException.class, () -> {
            userDAO.getUser("temp");
        });
    }
}
