package ui.serverfacade;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthtokenData login(String username, String password) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, new UserData(username, password, null), AuthtokenData.class, "");
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, new UserData(username, password,email), RegisterResult.class, "");
    }

    public String logout(String authToken) throws ResponseException {
        var path = "/session";
        HashMap<String, String> request = new HashMap<>();
        request.put("authorization", authToken);
        return this.makeRequest("DELETE", path, request, null, authToken);
    }

    public Object createGame(String gameName, String authToken) throws ResponseException{
        var path = "/game";
        return this.makeRequest("POST", path, new CreateGameRequest(gameName, authToken), CreateGameResult.class, authToken);
    }

    public Object listGames(String authToken)throws ResponseException{
        var path ="/game";
        return this.makeRequest("GET", path, null, GamesList.class, authToken);
    }

    public Object joinGame(Integer gameID, String playerColor, String authToken) throws ResponseException{
        var path = "/game";
        System.out.println("Making request to: " + serverUrl + path);
        System.out.println("Auth token: " + authToken);
        System.out.println("Join request: " + playerColor + " " + gameID);

        return this.makeRequest("PUT", path, new JoinGameRequest(playerColor, gameID), String.class, authToken);
    }

    public void clearDB() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null, "");
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if(!authToken.isEmpty()){
                http.addRequestProperty("authorization", authToken);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException("other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}