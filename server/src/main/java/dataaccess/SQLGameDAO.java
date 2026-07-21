package dataaccess;
import model.GameData;
import java.util.Collection;

import java.sql.SQLException;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(255) DEFAULT NULL,
            blackUsername VARCHAR(255) DEFAULT NULL,
            gameName VARCHAR(255) NOT NULL,
            game TEXT NOT NULL,
            PRIMARY KEY (gameID),
            FOREIGN KEY (whiteUsername) REFERENCES user(username) ON DELETE SET NULL,
            FOREIGN KEY (blackUsername) REFERENCES user(username) ON DELETE SET NULL
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
    public int createGame(String gameName) throws DataAccessException {
        // implement
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        // implement
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        // implement
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // implement
    }

    @Override
    public void clear() throws DataAccessException {
        // implement
    }
}
