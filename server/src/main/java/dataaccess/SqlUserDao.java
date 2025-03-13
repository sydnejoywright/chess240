package dataaccess;

import model.*;
import exception.ResponseException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.UUID;

public class SqlUserDao implements UserDAO {

    public SqlUserDao() throws ResponseException, DataAccessException {
        DatabaseManager.configureDatabase();
    }
    //Retrieve a user with the given username.
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username=?";
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
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            var ps = conn.prepareStatement(statement);

            ps.setString(1, userData.username);
            ps.setString(2, userData.password);
            ps.setString(3, userData.email);
            ps.executeUpdate();

        }catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clearData() throws ResponseException {
        try(var conn = DatabaseManager.getConnection();){
            var statement = "TRUNCATE users";
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        }catch (SQLException | DataAccessException e){
            throw new ResponseException(e.getMessage());
        }
    }





}




