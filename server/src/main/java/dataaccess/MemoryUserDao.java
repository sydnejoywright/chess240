package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.ArrayList;
import java.util.List;

public class MemoryUserDao implements UserDAO {
    List<UserData> currentUsers = new ArrayList<>();

    //Retrieve a user with the given username.
    public UserData getUser(String username){
        for(UserData user : currentUsers){
            if(username.equals(user.username)){
                return user;
            }
        }
        return null;
    }

    public void createUser(UserData userData){
        currentUsers.add(userData);
    }

    public void clearData(){
        currentUsers.clear();
    }

}
