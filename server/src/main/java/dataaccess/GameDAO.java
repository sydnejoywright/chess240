package dataaccess;
import exception.ResponseException;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public interface GameDAO {
    //Updates a chess game. It should replace the chess game
    // string corresponding to a given gameID. This is used when
    // players join a game or when a move is made.
    void updateGame(GameData gameData) throws DataAccessException;

    //Create a new game.
    int createGame(GameData gameData) throws DataAccessException;

    //Retrieve a specified game with the given game ID.
    GameData getGame(int gameID) throws DataAccessException;

    //Retrieve all games.
    List<GameData> listAllGames() throws DataAccessException;

    public void clearData() throws ResponseException;

}
