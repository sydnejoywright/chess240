package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.util.ArrayList;
import java.util.List;

public interface UserDAO {
    List<UserData> CurrentUsers = new ArrayList<>();

    //Retrieve a user with the given username.
    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    void clearData() throws ResponseException;

}
