package server;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import model.AuthtokenData;
import model.UserData;
import service.UserService;
import spark.*;
import java.util.UUID;

public class Server {
    UserDAO userDao = new UserDAO();
    UserService userService = new UserService(userDao);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (request, response) -> {

            //handle delete request
            return "Database deleted";
        });


        Spark.post("/user", (request, response) -> {
            var newUser = new Gson().fromJson(request.body(), UserData.class);
            AuthtokenData registeredUser = (AuthtokenData) userService.registerUser(newUser, userDao);
            return registeredUser;
        });


        Spark.post("/session", (request, response) -> {
            //handle
            return "User Logged in";
        });


        Spark.delete("/session", ((request, response) -> {
            //handle logout
            return "User logged out";
        }));

        Spark.get("/game", ((request, response) -> {
            //handle listing games
            return "games";
        }));


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
