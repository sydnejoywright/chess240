package dataaccess;

import exception.ResponseException;
import model.*;

import java.util.Collection;

public interface DataAccess {
    UserData addUser(UserData user) throws ResponseException;
    UserData getUser(UserData user) throws ResponseException;
    UserData deleteUser(UserData user) throws ResponseException;

    AuthtokenData addAuth(AuthtokenData auth) throws ResponseException;
    AuthtokenData getAuth(AuthtokenData auth) throws ResponseException;
    AuthtokenData deleteAuth(AuthtokenData auth) throws ResponseException;

    GameData addGame(GameData game) throws ResponseException;
    GameData getGame(GameData game) throws ResponseException;
    GameData deleteGame(GameData game) throws ResponseException;
    GameData listGames(GameData game) throws ResponseException;

}