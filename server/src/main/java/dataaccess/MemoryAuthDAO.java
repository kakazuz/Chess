package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if(auths.containsKey(auth.authToken())) {
            throw new DataAccessException("Already Authorized");
        }
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
       AuthData auth = auths.get(authToken);
       if(auth == null) {
           throw new DataAccessException("Null AuthToken");
       }
       return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Unauthorized");
        }
        auths.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }
}
