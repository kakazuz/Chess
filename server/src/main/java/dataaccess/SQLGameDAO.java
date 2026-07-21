package dataaccess;
import model.GameData;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

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
