package service;
import dataaccess.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Register - Positive")
    void registerPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("user1", "pass123", "email@test.com");
        RegisterLoginResult result = userService.register(request);

        assertEquals("user1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("Register - Negative")
    void registerNegativeAlreadyTaken() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("user1", "pass123", "email@test.com");
        userService.register(request);

        assertThrows(DataAccessException.class, () -> userService.register(request));
    }

    // ==================== LOGIN ====================
    @Test
    @DisplayName("Login - Positive")
    void loginPositive() throws DataAccessException {
        userService.register(new RegisterRequest("user1", "pass123", "email@test.com"));
        RegisterLoginResult result = userService.login(new LoginRequest("user1", "pass123"));

        assertEquals("user1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("Login - Negative")
    void loginNegativeWrongPassword() throws DataAccessException {
        userService.register(new RegisterRequest("user1", "pass123", "email@test.com"));

        assertThrows(DataAccessException.class, () -> userService.login(new LoginRequest("user1", "wrongpass")));
    }

    @Test
    @DisplayName("Logout - Positive")
    void logoutPositive() throws DataAccessException {
        RegisterLoginResult reg = userService.register(new RegisterRequest("user1", "pass123", "email@test.com"));
        userService.logout(reg.authToken());

        assertThrows(DataAccessException.class, () -> userService.logout(reg.authToken()));
    }

    @Test
    @DisplayName("Logout - Negative")
    void logoutNegativeInvalidToken() throws DataAccessException {
        RegisterLoginResult result = userService.register(new RegisterRequest("user1", "pass123", "email@test.com"));

        assertThrows(DataAccessException.class, () -> userService.logout("invalid-token-12345"));
    }
}
