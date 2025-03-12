package dataaccess;

import exception.ResponseException;
import model.*;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlAuthDAO implements AuthDAO {
    //Create a new authorization.

    public AuthtokenData createAuth(String username){


    }

    //Retrieve an authorization given an authToken.
    public AuthtokenData getAuth(AuthtokenData authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM  WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken.username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String auth = rs.getString("authToken");
                        return new AuthtokenData(rs.getString("username"), rs.getString("authToken"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;

    }

    //Delete an authorization so that it is no longer valid.

    public void deleteAuth(AuthtokenData authtokenData) throws ResponseException{
        var statement = "DELETE FROM pet WHERE id=?";
        executeUpdate(statement, id);
    }

    public void clearData(){

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}


