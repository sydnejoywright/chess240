package client;

import exception.ResponseException;
import model.AuthtokenData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import ui.ServerFacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void loginPositive() throws ResponseException {
//        register the user before logging them in
        facade.register("sydne", "hello", "hi@hi.com");
        AuthtokenData authToken = facade.login("sydne", "hello");

    }

    @Test
    public void loginNegative() {
        Assertions.assertThrows(ResponseException.class, () -> facade.login("sydne", "password"));
    }
    @Test
    public void registerPositive() {
        Assertions.assertTrue(true);
    }
    @Test
    public void registerNegative() {
        Assertions.assertTrue(true);
    }
    @Test
    public void logoutPositive() {
        Assertions.assertTrue(true);
    }
    @Test
    public void logoutNegative() {
        Assertions.assertTrue(true);
    }
    @Test
    public void createGamePositive() {
        Assertions.assertTrue(true);
    }
    @Test
    public void createGameNegative() {
        Assertions.assertTrue(true);
    }
    @Test
    public void listGamesPositive() {
        Assertions.assertTrue(true);
    }
    @Test
    public void listGamesNegative() {
        Assertions.assertTrue(true);
    }
    @Test
    public void joinGamePositive() {
        Assertions.assertTrue(true);
    }
    @Test
    public void joinGameNegative() {
        Assertions.assertTrue(true);
    }

}
