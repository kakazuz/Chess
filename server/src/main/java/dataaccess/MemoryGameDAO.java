package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = nextGameID;
        nextGameID++;

        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, newGame);

        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if(!games.containsKey(gameID)) {
            throw new DataAccessException("No Game found");
        }
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Bad request");
        }
        games.put(game.gameID(), game);
    }
}
