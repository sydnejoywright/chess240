package server;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.*;
import service.GameService;
import service.UserService;
import spark.*;
import websocket.WebSocketFacade;

import java.util.HashMap;

public class Server {
    AuthDAO authDao;
    UserDAO userDao;
    GameDAO gameDao;
    WebSocketFacade webSocketFacade;


    {try{
        authDao = new SqlAuthDAO();
        userDao = new SqlUserDao();
        gameDao = new SqlGameDao();
        webSocketFacade = new WebSocketFacade(authDao, gameDao, userDao);
    }catch(ResponseException | DataAccessException e)
    {throw new RuntimeException(e);}}
    UserService userService = new UserService(userDao, authDao);
    GameService gameService = new GameService(userDao, authDao, gameDao);
    public int run(int desiredPort) {Spark.port(desiredPort);Spark.staticFiles.location("web");
    Spark.webSocket("/ws", webSocketFacade);

//DELETE DATABASE........................................................................................................
        Spark.delete("/db", (request, response) -> {
            response.status(200); gameService.clearData(); return "";
        });
//REGISTER NEW USER......................................................................................................
        Spark.post("/user", (request, response) -> {
            UserData newUser = new Gson().fromJson(request.body(), UserData.class);
            try {AuthtokenData result = (AuthtokenData) userService.registerUser(newUser);response.status(200);
                return new Gson().toJson(result);
            } catch (ResponseException e) {
                if(e.getMessage().equals("Error: already taken")){response.status(403);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                if(e.getMessage().equals("Error: bad request")){response.status(400);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                else{response.status(500);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}}
        });
//LOGIN USER ...........................................................................................................
        Spark.post("/session", (request, response) -> {
            UserData newUser = new Gson().fromJson(request.body(), UserData.class);
            try {AuthtokenData result = userService.loginUser(newUser);
                response.status(200);
                return new Gson().toJson(result);}
            catch (ResponseException e){
                if(e.getMessage().equals("Error: unauthorized")){response.status(401);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                else if(e.getMessage().equals("Error: user not found")){response.status(401);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                else {response.status(500);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}}
        });
//LOGOUT USER............................................................................................................
        Spark.delete("/session", ((request, response) -> {
            String paramAuth = request.headers("authorization");
            AuthtokenData newAuth = new AuthtokenData(null, paramAuth);
            try{userService.logoutUser(newAuth);response.status(200); return "";
            } catch (ResponseException f) {
                if(f.getMessage().equals("Error: unauthorized")){response.status(401);
                    return new Gson().toJson(new ErrorResponse(f.getMessage()));}
                else{response.status(500);
                    return new Gson().toJson(new ErrorResponse(f.getMessage()));}}
        }));
//LIST GAMES.............................................................................................................
        Spark.get("/game", ((request, response) -> {
            String paramAuth = request.headers("authorization");
            AuthtokenData newAuth = new AuthtokenData(null, paramAuth);
            try {
                response.status(200);
                var temp = gameService.listGames(newAuth);
                GamesList gamesList = new GamesList(temp);
                return new Gson().toJson(gamesList);
                }
            catch (ResponseException g){
                if(g.getMessage().equals("Error: unauthorized")){response.status(401);
                    return new Gson().toJson(new ErrorResponse(g.getMessage()));}
                else{response.status(500);
                    return new Gson().toJson(new ErrorResponse(g.getMessage()));}}
        }));
//CREATE GAME............................................................................................................
        Spark.post("/game", ((request, response) -> {
            String paramAuth = request.headers("authorization");
            String gameName = new Gson().fromJson(request.body(), GameName.class).gameName();
            try{
                CreateGameRequest newGameRequest = new CreateGameRequest(gameName, paramAuth);
                response.status(200);
                return new Gson().toJson(gameService.createGame(newGameRequest));}
            catch(ResponseException e){
                if(e.getMessage().equals("Error: bad request")) {response.status(400);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                if(e.getMessage().equals("Error: unauthorized")){response.status(401);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                else{response.status(500);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}}
        }));
//JOIN GAME..............................................................................................................
        Spark.put("/game", ((request, response) -> {
            JoinGameRequest joinGameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
            String paramAuth = request.headers("authorization");
            try {
                AuthtokenData authToken = new AuthtokenData(null, paramAuth);
                gameService.joinGame(joinGameRequest, authToken);
                response.status(200); return "";}
            catch(ResponseException e){
                if(e.getMessage().equals("Error: bad request")){response.status(400);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                if(e.getMessage().equals("Error: unauthorized")){response.status(401);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                if(e.getMessage().equals("Error: already taken")){response.status(403);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}
                else{ response.status(500);
                    return new Gson().toJson(new ErrorResponse(e.getMessage()));}}}));
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();}
    public void stop() {
        Spark.stop();
        Spark.awaitStop();}
}
