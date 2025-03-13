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
            DatabaseManager.configureDatabase();
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
}


