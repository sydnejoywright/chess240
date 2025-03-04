package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthtokenData;
import model.UserData;
import service.ClearDataService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.List;
import java.util.UUID;

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

//LOG IN USER ...........................................................................................................

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
            //handle create game
            return "game";
        }));


        Spark.put("/game", ((request, response) -> {
            //handle join game;
            return "Game joined";
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
