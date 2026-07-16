package service;

import dataaccess.*;
import org.junit.jupiter.api.*;

class ClearServiceTest {

    private ClearService clearService;

    @BeforeEach
    void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    @DisplayName("Clear - Positive")
    void clearPositive() throws DataAccessException {
        clearService.clear();
    }
}
