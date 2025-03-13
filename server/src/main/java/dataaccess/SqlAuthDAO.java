package dataaccess;

import exception.ResponseException;
import model.*;

import com.google.gson.Gson;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlAuthDAO implements AuthDAO {
    //Create a new authorization.

    public SqlAuthDAO() throws ResponseException, DataAccessException{
            configureDatabase();
    }

    public AuthtokenData createAuth(String username) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
            var ps = conn.prepareStatement(statement);

            String authToken = UUID.randomUUID().toString();
            ps.setString(1, username);
            ps.setString(2, authToken);
            ps.executeUpdate();

            return new AuthtokenData(username, authToken);
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //Retrieve an authorization given an authToken.
    public AuthtokenData getAuth(AuthtokenData authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken.authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthtokenData(rs.getString("username"), rs.getString("authToken"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        System.out.println("We have a problem here");
        return null;

    }

    //Delete an authorization so that it is no longer valid.

    public void deleteAuth(AuthtokenData authtokenData) throws ResponseException{
        try{
            if(getAuth(authtokenData) == null){
                throw new ResponseException("Error: unauthorized");
            }
            System.out.println(authtokenData);
            try(var conn = DatabaseManager.getConnection();){
                var statement = "DELETE FROM auths WHERE authToken=?";

                var ps = conn.prepareStatement(statement);
                ps.setString(1, authtokenData.authToken);
                ps.executeUpdate();
        }
    }catch (Exception e){
            throw new ResponseException(e.getMessage());
        }
    }

    public void clearData() throws ResponseException{
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
            CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
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


