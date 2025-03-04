package service;
//each service method receives a request object containing the info it needs to do its work and it returns a result obejct containing the output of the method.

import dataaccess.UserDAO;
import model.AuthtokenData;
import model.UserData;

public class UserService {
    private final UserDAO userDao;

    public UserService(UserDAO userDao){
        this.userDao = userDao;
    }
    public Object registerUser(UserData userData, UserDAO userDao){
        UserData findUserDataByUsername = userDao.getUser(userData.username);
        if(findUserDataByUsername == null){
            userDao.createUser(userData);

        }
        else{
            //throw Error;
        }
    }
    public Object loginUser(){}
    public Object logoutUser(){}

}
