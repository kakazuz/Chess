package dataaccess;
import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken),
            FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE
        )
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        // implement
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        // implement
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // implement
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
