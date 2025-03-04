package dataaccess;

import model.UserData;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {
    List<UserData> currentUsers = new ArrayList<>();

    //Retrieve a user with the given username.
    public UserData getUser(String username){
        for(UserData user : currentUsers){
            if(user.username == username){
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
