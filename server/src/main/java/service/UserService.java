package service;
//each service method receives a request object containing the info it needs
// to do its work and it returns a result obejct containing the output of the method.

import dataaccess.AuthDAO;
import dataaccess.MemoryUserDao;
import exception.ResponseException;
import model.AuthtokenData;
import model.UserData;


public class UserService {
    private final MemoryUserDao memoryUserDao;
    private final AuthDAO authDao;

    public UserService(MemoryUserDao memoryUserDao, AuthDAO authDao){
        this.memoryUserDao = memoryUserDao;
        this.authDao = authDao;
    }
    public AuthtokenData registerUser(UserData userData) throws ResponseException {
        UserData findUserDataByUsername = memoryUserDao.getUser(userData.username);
        if (userData.username == null || userData.password == null || userData.email == null){
            throw new ResponseException("Error: bad request");
        }
        if(findUserDataByUsername == null){
            memoryUserDao.createUser(userData);
            AuthtokenData ret = authDao.createAuth(userData.username);
            return ret;
        }
        else{
            throw new ResponseException("Error: already taken");
        }
    }

    public AuthtokenData loginUser(UserData userData) throws ResponseException {
        UserData findUserDataByUsername = memoryUserDao.getUser(userData.username);
        if (findUserDataByUsername == null){
            throw new ResponseException("Error: unauthorized");
        }
        if (findUserDataByUsername.password.equals(userData.password)){
            AuthtokenData ret = authDao.createAuth(userData.username);
            return ret;
        }
        else{
            throw new ResponseException("Error: unauthorized");
        }
    }

    public void logoutUser(AuthtokenData authToken) throws ResponseException {

        if(authToken != null){
            authDao.deleteAuth(authToken);
        }
        else{
            throw new ResponseException("Error: unauthorized");
        }

    }


}
