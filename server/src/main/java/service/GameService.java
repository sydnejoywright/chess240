package service;
import dataaccess.*;
import exception.ResponseException;
import model.*;

import javax.swing.plaf.synth.SynthDesktopIconUI;
import javax.xml.crypto.Data;
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
        try {
            AuthtokenData findAuth = authDao.getAuth(authToken);
            if (findAuth != null) {
                return gameDao.listAllGames();
            } else {
                throw new ResponseException("Error: unauthorized");
            }
        }catch(DataAccessException e){
            System.out.println("Error in listGames in GameService.java: " + e.getMessage());
        }
        return null;
    }

    public CreateGameResult createGame(CreateGameRequest gameRequest) throws ResponseException {
        if(gameRequest.gameName() == null || gameRequest.authToken() == null){
            throw new ResponseException("Error: bad request");
        }

        AuthtokenData buildAuth = new AuthtokenData(null, gameRequest.authToken());
        try {
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
        }catch(DataAccessException e){
            System.out.println("Error in createGame in GameService.java: " + e.getMessage());
        }
        return null;
    }

    public void clearData(){
        try {
            userDao.clearData();
            authDao.clearData();
            gameDao.clearData();
        } catch(ResponseException e){
            System.out.println("Error in clearData in GameService.java: " + e.getMessage());

        }
    }

    public void joinGame(JoinGameRequest gameRequest, AuthtokenData authToken) throws ResponseException {
        if (authToken.authToken == null || gameRequest.playerColor() == null || gameRequest.gameID() == null) {
            throw new ResponseException("Error: bad request");
        }

        try {
            AuthtokenData foundAuth = authDao.getAuth(authToken);
            if (foundAuth == null) {
                throw new ResponseException("Error: unauthorized");
            }
            if (!gameRequest.playerColor().equalsIgnoreCase("WHITE") && !gameRequest.playerColor().equalsIgnoreCase("BLACK")) {
                System.out.println(gameRequest.playerColor());
                System.out.print("touched ya");
                throw new ResponseException("Error: bad request");
            } else {
                GameData gameData = gameDao.getGame(gameRequest.gameID());
                System.out.println("poopoo doodoo " + gameData);

                if(gameData == null){
                    System.out.println("touched here");
                    throw new ResponseException("Error: bad request");
                }
                if (gameRequest.playerColor().equalsIgnoreCase("WHITE")) {
                    if (gameData.getWhiteUsername() == null) {
                        gameData.setWhiteUsername(foundAuth.username);
                        gameDao.updateGame(gameData);
                    } else {
                        throw new ResponseException("Error: already taken");
                    }
                } else {
                    if (gameData.getBlackUsername() == null) {
                        gameData.setBlackUsername(foundAuth.username);
                        gameDao.updateGame(gameData);
                    } else {
                        throw new ResponseException("Error: already taken");
                    }
                }
            }
        }catch(DataAccessException e){
            System.out.println("Error in joinGame in GameService.java: " + e.getMessage());
        }

    }
}
