package dataaccess;

import exception.ResponseException;
import model.*;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.sql.*;
import java.util.List;

public class SqlGameDao implements GameDAO{
    private int Counter = 1;
    public SqlGameDao() throws ResponseException, DataAccessException {
        DatabaseManager.configureDatabase();
    }

    //Updates a chess game. It should replace the chess game
    // string corresponding to a given gameID. This is used when
    // players join a game or when a move is made.
    public void updateGame(GameData gameData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM games WHERE gameID = ?";
            var ps = conn.prepareStatement(statement);
            ps.setInt(1, gameData.getGameID());
            ps.executeUpdate();

            statement = "INSERT INTO games (gameID, json) VALUES (?,?)";
            ps = conn.prepareStatement(statement);

            ps.setInt(1, gameData.getGameID());
            ps.setString(2, new Gson().toJson(gameData));
            ps.executeUpdate();
            //createGame(gameData);

        }catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //Create a new game.
    public int createGame(GameData gameData) throws DataAccessException {
        if (getGame(gameData.getGameID()) != null) {
            throw new DataAccessException("This game already exists");
        }
        gameData.setGameID(Counter);
        Counter += 1;
        String game = new Gson().toJson(gameData);
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO games (gameID, json) VALUES (?,?)";
            var ps = conn.prepareStatement(statement);

            ps.setInt(1, gameData.getGameID());
            ps.setString(2, game);
            ps.executeUpdate();
            System.out.println("i witowy did dis");
            return gameData.getGameID();

        }catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //Retrieve a specified game with the given game ID.
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID=?";
            System.out.println(gameID);
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Gson().fromJson(rs.getString("json"), GameData.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    //Retrieve all games.
    public List<GameData> listAllGames() throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gameList.add(new Gson().fromJson(rs.getString("json"), GameData.class));
                        System.out.println("Game: " + rs.getString("json"));
                    }
                }
                System.out.println("Game List: " + gameList);
                return gameList;
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clearData() throws ResponseException {
        Counter = 1;
        try(var conn = DatabaseManager.getConnection();){
            var statement = "TRUNCATE games";
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        }catch (SQLException | DataAccessException e){
            throw new ResponseException(e.getMessage());
        }
    }
}
