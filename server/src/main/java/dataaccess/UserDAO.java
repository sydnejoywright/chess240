package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.List;

public interface UserDAO {
    List<UserData> currentUsers = new ArrayList<>();

    //Retrieve a user with the given username.
    UserData getUser(String username);

    void createUser(UserData userData);

    void clearData();

}
