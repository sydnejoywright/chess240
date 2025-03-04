package service;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.*;
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

    public CreateGameResult createGame(CreateGameRequest gameRequest) throws ResponseException {
        if(gameRequest.gameName() == null || gameRequest.authToken() == null){
            throw new ResponseException("Error: bad request");
        }

        AuthtokenData buildAuth = new AuthtokenData(null, gameRequest.authToken());
        AuthtokenData foundAuth = authDao.getAuth(buildAuth);

        if(foundAuth != null){
            GameData newGame = new GameData();
            newGame.setGameName(gameRequest.gameName());
            int gameID = gameDao.createGame(newGame);
            return new CreateGameResult(gameID);
        }
        else{
            throw new ResponseException("Error: unauthorized");
        }
    }


    public Object joinGame(JoinGameRequest gameRequest) throws ResponseException{
        if(gameRequest.authToken() == null || gameRequest.playerColor() == null || gameRequest.gameID() == null){
            throw new ResponseException("Error: bad request");
        }

        AuthtokenData buildAuth = new AuthtokenData(null, gameRequest.authToken());
        AuthtokenData foundAuth = authDao.getAuth(buildAuth);
        if(foundAuth == null){
            throw new ResponseException("Error: unauthorized");
        }
        else{
            GameData gameData = gameDao.getGame(gameRequest.gameID());

        }

    }
}
