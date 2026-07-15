package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
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
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
        }

        return result;
    }

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException{
        if (request.playerColor() == null || request.playerColor().isBlank() ||
                request.gameID() == null) {
            throw new DataAccessException("bad request");
        }
        authDAO.getAuth(authToken);
        AuthData auth = authDAO.getAuth(authToken);
        String username = auth.username();

        GameData game = gameDAO.getGame(request.gameID());
        GameData updatedGame;

        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null && !game.whiteUsername().isBlank()) {
                throw new DataAccessException("already taken");
            }
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName());
        }
        else if (request.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null && !game.blackUsername().isBlank()) {
                throw new DataAccessException("already taken");
            }
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName());
        } else {
            throw new DataAccessException("bad request");
        }
        gameDAO.updateGame(updatedGame);
    }

}
