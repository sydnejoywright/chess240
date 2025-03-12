package dataaccess;

import exception.ResponseException;
import model.AuthtokenData;
import model.GameData;
import model.UserData;

import java.sql.*;


public class AuthSqlDataAccess implements AuthDAO {
    //Create a new authorization.
    @Override
    public AuthtokenData createAuth(String username){
    }

    //Retrieve an authorization given an authToken.
    public AuthtokenData getAuth(AuthtokenData authToken){

    }

    //Delete an authorization so that it is no longer valid.

    public void deleteAuth(AuthtokenData authtokenData) throws ResponseException{

    }

    public void clearData(){

    }
}


