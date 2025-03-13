package dataaccess;

import model.*;
import exception.ResponseException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.UUID;

public class SqlUserDao implements UserDAO {

    public SqlUserDao() throws ResponseException, DataAccessException {
        configureDatabase();
    }
    //Retrieve a user with the given username.
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public void createUser(UserData userData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?)";
            var ps = conn.prepareStatement(statement);

            String authToken = UUID.randomUUID().toString();
            ps.setString(1, userData.username);
            ps.setString(2, userData.password);
            ps.setString(3, userData.email);

        }catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clearData() throws ResponseException {
        try(var conn = DatabaseManager.getConnection();){
            var statement = "TRUNCATE auths";
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        }catch (SQLException | DataAccessException e){
            throw new ResponseException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}




