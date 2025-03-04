package dataaccess;
import model.GameData;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    List<GameData> currentGames = new ArrayList<>();
    //Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
    public void updateGame(){}

    //Create a new game.
    public void createGame(){}

    //Retrieve a specified game with the given game ID.
    public void getGame(){}

    //Retrieve all games.
    public List<GameData> listAllGames(){
        return currentGames;
    }

    public void clearData(){
        currentGames.clear();
    }

}
