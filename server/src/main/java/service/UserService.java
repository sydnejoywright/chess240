package service;
//each service method receives a request object containing the info it needs to do its work and it returns a result obejct containing the output of the method.

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthtokenData;
import model.UserData;

public class UserService {
    private final UserDAO userDao;
    private final AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao){
        this.userDao = userDao;
        this.authDao = authDao;
    }
    public AuthtokenData registerUser(UserData userData) throws ResponseException {
        UserData findUserDataByUsername = userDao.getUser(userData.username);
        if(findUserDataByUsername == null){
            userDao.createUser(userData);
            AuthtokenData ret = authDao.createAuth(userData.username);
            return ret;
        }
        else{
            throw new ResponseException("User already exists: " + userData.username);
        }
    }

    public AuthtokenData loginUser(UserData userData) throws ResponseException {
        UserData findUserDataByUsername = userDao.getUser(userData.username);
        if (findUserDataByUsername.password == userData.password){
            AuthtokenData ret = authDao.createAuth(userData.username);
            return ret;
        }
        else{
            throw new ResponseException("Incorrect Password");
        }
    }

    public void logoutUser(AuthtokenData authToken) throws ResponseException {
        authDao.deleteAuth(authToken);
    }


}
