package server;

import service.ClearDataService;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (request, response) -> {

            //handle delete request
            return "Database deleted";
        });


        Spark.post("/user", (request, response) -> {
            //handle user creation
            return "User Registered";
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
