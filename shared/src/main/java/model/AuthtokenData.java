package model;

public class AuthtokenData {
    public String authToken;

    @Override
    public String toString() {
        return "AuthtokenData{" +
                "authToken='" + authToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public String username;

    public AuthtokenData(String username, String authToken){
        this.authToken = authToken;
        this.username = username;
    }

}
