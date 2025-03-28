package service;
//each service method receives a request object containing the info it needs
// to do its work and it returns a result obejct containing the output of the method.

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDao;
import exception.ResponseException;
import model.AuthtokenData;
import model.UserData;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDao;
    private final AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao){
        this.userDao = userDao;
        this.authDao = authDao;
    }
    public AuthtokenData registerUser(UserData userData) throws ResponseException {
        try{UserData findUserDataByUsername = userDao.getUser(userData.username);
            if (userData.username == null || userData.password == null || userData.email == null) {
                throw new ResponseException("Error: bad request");
            }
            if (findUserDataByUsername == null) {
                try {
                    userDao.createUser(new UserData (userData.username, BCrypt.hashpw(userData.password, BCrypt.gensalt()), userData.email));
                    AuthtokenData ret = authDao.createAuth(userData.username);
                    return ret;
                } catch (DataAccessException e) {
                    System.out.println("Error in registerUser in UserService.java: " + e.getMessage());
                }
            } else {
                throw new ResponseException("Error: already taken");
            }

            return null;
        } catch (DataAccessException e) {
                throw new RuntimeException(e);
        }
    }

        public AuthtokenData loginUser(UserData userData) throws ResponseException, DataAccessException {

        UserData findUserDataByUsername = userDao.getUser(userData.username);
        if (findUserDataByUsername == null){
            throw new ResponseException("Error: user not found");
        }
        if(BCrypt.checkpw(userData.password,findUserDataByUsername.password)){
            try {
                AuthtokenData ret = authDao.createAuth(userData.username);
                return ret;
            }catch(DataAccessException e){
                System.out.println("Error in loginUser in UserService.java: " + e.getMessage());
            }
        }
        else{
            throw new ResponseException("Error: unauthorized");
        }
        return null;
    }

    public void logoutUser(AuthtokenData authToken) throws ResponseException {
        if(authToken != null){
            System.out.println(authToken);
            authDao.deleteAuth(authToken);
        }
        else{
            throw new ResponseException("Error: unauthorized");
        }
    }
}
