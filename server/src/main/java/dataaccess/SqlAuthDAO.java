package dataaccess;

import exception.ResponseException;
import model.*;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlAuthDAO implements AuthDAO {
    //Create a new authorization.

    public SqlAuthDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public AuthtokenData createAuth(String username) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("INSERT INTO auths (authToken, username) VALUES (?, ?)");
            String authToken = UUID.randomUUID().toString();
            var id = executeUpdate(statement, username, authToken);
            return new AuthtokenData(username, authToken);
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //Retrieve an authorization given an authToken.
    public AuthtokenData getAuth(AuthtokenData authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM auths WHERE username=?";
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
        var statement = "DELETE FROM auths WHERE username=?";
        executeUpdate(statement, authtokenData.username);
    }

    public void clearData(){

    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
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


