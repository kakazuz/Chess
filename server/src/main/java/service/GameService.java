package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

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

    public Collection<GameDataForList>  listGames(String authToken) throws DataAccessException {
        authDAO.getAuth(authToken);
        Collection<GameData> allGames = gameDAO.listGames();
        Collection<GameDataForList> result = new ArrayList<>();

        for (GameData game : allGames) {
            result.add(new GameDataForList(
                    game.gameID(),
                    game.whiteUsername() != null ? game.whiteUsername() : "",
                    game.blackUsername() != null ? game.blackUsername() : "",
                    game.gameName()
            ));
        }

        return result;
    }

}
