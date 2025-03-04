package dataaccess;

import model.AuthtokenData;
import model.UserData;

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
    public void getAuth(){}

    //Delete an authorization so that it is no longer valid.
    public void deleteAuth(AuthtokenData authtokenData){
        for(AuthtokenData tokenData: currentAuths){
            if(tokenData == authtokenData){
                currentAuths.remove(tokenData);
            }
        }
    }

    public void clearData(){
        currentAuths.clear();
    }

}
