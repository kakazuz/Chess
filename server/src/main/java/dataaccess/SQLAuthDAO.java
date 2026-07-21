package dataaccess;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

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
        // implement
    }
}
