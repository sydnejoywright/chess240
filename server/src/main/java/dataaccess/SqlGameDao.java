package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SqlGameDao implements GameDAO{
    List<GameData> currentGames = new ArrayList<>();
    HashMap<Integer, GameData> gameIds = new HashMap<>();
    private int counter = 1;

    //Updates a chess game. It should replace the chess game
    // string corresponding to a given gameID. This is used when
    // players join a game or when a move is made.
    public void updateGame(GameData gameData){

    }

    //Create a new game.
    public int createGame(GameData gameData){

    }

    //Retrieve a specified game with the given game ID.
    public GameData getGame(int gameID){

    }

    //Retrieve all games.
    public List<GameData> listAllGames(){

    }

    public void clearData(){

    }

}
