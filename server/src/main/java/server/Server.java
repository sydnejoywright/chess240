package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    UserDAO userDao = new UserDAO();
    AuthDAO authDao = new AuthDAO();
    GameDAO gameDao = new GameDAO();

    UserService userService = new UserService(userDao, authDao);
    GameService gameService = new GameService(userDao, authDao, gameDao);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

//DELETE DATABASE........................................................................................................
        Spark.delete("/db", (request, response) -> {
            userDao.clearData();
            authDao.clearData();
            gameDao.clearData();
            return null;
        });

//REGISTER NEW USER......................................................................................................
        Spark.post("/user", (request, response) -> {
            var newUser = new Gson().fromJson(request.body(), UserData.class);
            AuthtokenData result = (AuthtokenData) userService.registerUser(newUser);
            return result;
        });

//LOGIN USER ...........................................................................................................

        Spark.post("/session", (request, response) -> {
            var newUser = new Gson().fromJson(request.body(), UserData.class);
            AuthtokenData result = userService.loginUser(newUser);
            return result;
        });

//LOGOUT USER............................................................................................................
        Spark.delete("/session", ((request, response) -> {
            AuthtokenData newAuth = new Gson().fromJson(request.body(), AuthtokenData.class);
            userService.logoutUser(newAuth);
            return "success response";
        }));

//LIST GAMES.............................................................................................................
        Spark.get("/game", ((request, response) -> {
            AuthtokenData newAuth = new Gson().fromJson(request.body(), AuthtokenData.class);
            return gameService.listGames(newAuth);
        }));

//CREATE GAME............................................................................................................
        Spark.post("/game", ((request, response) -> {
            CreateGameRequest newGameRequest = new Gson().fromJson(request.body(), CreateGameRequest.class);
            return gameService.createGame(newGameRequest);
        }));

//JOIN GAME..............................................................................................................
        Spark.put("/game", ((request, response) -> {
            JoinGameRequest joinGameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
            gameService.joinGame(joinGameRequest);
            return "success response";
        }));


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
