package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        if (request.gameName() == null || request.gameName().isBlank()) {
            throw new DataAccessException("bad request");
        }
        authDAO.getAuth(authToken);

        int gameID = gameDAO.createGame(request.gameName());

        return new CreateGameResult(gameID);
    }

}
