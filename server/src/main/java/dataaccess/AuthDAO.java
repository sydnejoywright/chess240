package dataaccess;

import exception.ResponseException;
import model.AuthtokenData;
import model.UserData;

import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface  AuthDAO {

    //Create a new authorization.
    AuthtokenData createAuth(String username);

    //Retrieve an authorization given an authToken.
    AuthtokenData getAuth(AuthtokenData authToken) throws DataAccessException;

    //Delete an authorization so that it is no longer valid.
    void deleteAuth(AuthtokenData authtokenData) throws ResponseException;

    void clearData();

}
