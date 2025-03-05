package dataaccess;

import exception.ResponseException;
import model.AuthtokenData;
import model.UserData;

import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthDAO {
    List<AuthtokenData> currentAuths = new ArrayList<>();

    //Create a new authorization.
    public AuthtokenData createAuth(String username){
        String authToken = UUID.randomUUID().toString();
        AuthtokenData authData = new AuthtokenData(username, authToken);
        currentAuths.add(authData);
        return authData;
    }

    //Retrieve an authorization given an authToken.
    public AuthtokenData getAuth(AuthtokenData authToken){
        for(AuthtokenData token: currentAuths){
            if(token.authToken.equals(authToken.authToken)){
                return token;
            }
        }
        return null;
    }

    //Delete an authorization so that it is no longer valid.
    public void deleteAuth(AuthtokenData authtokenData) throws ResponseException {
        for(AuthtokenData tokenData: currentAuths){
            if(tokenData.authToken.equals(authtokenData.authToken)){
                currentAuths.remove(tokenData);
                return;
            }
        }
        throw new ResponseException("Error: unauthorized");

    }

    public void clearData(){

        currentAuths.clear();
    }

}
