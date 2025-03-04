package service;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.AuthtokenData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;

import java.util.List;


public class GameService {
    private final UserDAO userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public GameService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao){
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public List<GameData> listGames(AuthtokenData authToken) throws ResponseException {
        AuthtokenData findAuth = authDao.getAuth(authToken);
        if(findAuth != null){
            return gameDao.listAllGames();
        }
        else{
            throw new ResponseException("Error: unauthorized");
        }
    }
//    public Object createGame(){}
//    public Object joinGame(){}
}
