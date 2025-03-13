//package dataaccess;
//
//import exception.ResponseException;
//import model.*;
//
//import com.google.gson.Gson;
//
//import javax.xml.crypto.Data;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.sql.*;
//import java.util.List;
//import java.util.UUID;
//
//import static java.sql.Statement.RETURN_GENERATED_KEYS;
//import static java.sql.Types.NULL;
//
//public class SqlGameDao implements GameDAO{
//
//    public SqlGameDao() throws ResponseException, DataAccessException {
//        configureDatabase();
//    }
//
//    //Updates a chess game. It should replace the chess game
//    // string corresponding to a given gameID. This is used when
//    // players join a game or when a move is made.
//    public void updateGame(GameData gameData){
//        try(var conn = DatabaseManager.getConnection()) {
//            var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
//            var ps = conn.prepareStatement(statement);
//
//            String authToken = UUID.randomUUID().toString();
//            ps.setString(1, username);
//            ps.setString(2, authToken);
//
//            return new AuthtokenData(username, authToken);
//        }catch (SQLException e) {
//            throw new DataAccessException(e.getMessage());
//        }
//    }
//
//    //Create a new game.
//    public int createGame(GameData gameData){
//
//    }
//
//    //Retrieve a specified game with the given game ID.
//    public GameData getGame(int gameID){
//
//    }
//
//    //Retrieve all games.
//    public List<GameData> listAllGames(){
//
//    }
//
//    public void clearData(){
//
//    }
//
//    private final String[] createStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS  games (
//              `gameID` int NOT NULL AUTO_INCREMENT
//              `whiteUsername` varchar(256) NOT NULL,
//              `blackUsername` varchar(256) NOT NULL,
//              `gameName` varchar(256) NOT NULL,
//              INDEX(gameID),
//            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
//            """
//    };
//
//    private void configureDatabase() throws ResponseException, DataAccessException {
//        DatabaseManager.createDatabase();
//        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException ex) {
//            throw new ResponseException(String.format("Unable to configure database: %s", ex.getMessage()));
//        }
//    }
//
//}
