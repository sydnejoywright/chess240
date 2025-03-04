package dataaccess;
import model.GameData;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class GameDAO {
    List<GameData> currentGames = new ArrayList<>();
    HashMap<Integer, GameData> gameIds = new HashMap<>();
    private int counter = 1;

    //Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
    public void updateGame(){}

    //Create a new game.
    public int createGame(GameData gameData){
        currentGames.add(gameData);
        gameIds.put(counter, gameData);
        counter +=1;
        return counter-1;
    }

    //Retrieve a specified game with the given game ID.
    public GameData getGame(int gameID){
        return currentGames.get(gameID);
        }

    //Retrieve all games.
    public List<GameData> listAllGames(){
        return currentGames;
    }

    public void clearData(){
        currentGames.clear();
    }

}
