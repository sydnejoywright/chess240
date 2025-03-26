package client;

import exception.ResponseException;
import model.AuthtokenData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.serverfacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clearDB();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void loginPositive() throws ResponseException {
//        register the user before logging them in
        facade.register("sydne", "hello", "hi@hi.com");
        Assertions.assertNotNull(facade.login("sydne", "hello").authToken);
    }

    @Test
    public void loginNegativeNotReg() {
        Assertions.assertThrows(ResponseException.class, () -> facade.login("sydne", "password"));
    }

    @Test
    public void loginNegativeBadCred() {
        Assertions.assertThrows(ResponseException.class, () -> facade.login(null,"password"));
    }
    @Test
    public void registerPositive() throws ResponseException {
        Assertions.assertNotNull(facade.register("sydne","joy", "wright").authToken());
    }
    @Test
    public void registerNegative() throws ResponseException {
        //try to register the same user twice
        facade.register("sydne", "hello", "hi@hi.com");
        Assertions.assertThrows(ResponseException.class, () -> facade.register("sydne", "hello", "hi@hi.com"));
    }
    @Test
    public void logoutPositive() throws ResponseException {
        facade.register("sydne", "joy", "wright");
        AuthtokenData authtokenData = facade.login("sydne", "joy");
        String result = facade.logout(authtokenData.authToken);
        System.out.println("Logout result: [" + result + "]");
        Assertions.assertNull(result);
    }
    @Test
    public void logoutNegative() {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("sydne"));
    }

    @Test
    public void logoutNegativeBADcRED() {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(null));
    }
    @Test
    public void createGamePositive() throws ResponseException {
        facade.register("sydne", "joy", "wright");
        AuthtokenData authtokenData = facade.login("sydne", "joy");
        facade.createGame("hello", authtokenData.authToken);
        Object list = facade.listGames(authtokenData.authToken);
        Assertions.assertTrue(list.toString().contains("hello"));
    }

    @Test
    public void createGameNegative() {
        //try to create a game without authorization
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame("sydne", null));
    }

    @Test
    public void listGamesPositive() throws ResponseException {
        facade.register("hehe", "haha", "hoho");
        AuthtokenData authtokenData = facade.login("hehe", "haha");
        facade.createGame("hello", authtokenData.authToken);
        Object list = facade.listGames(authtokenData.authToken);
        Assertions.assertTrue(list.toString().contains("hello"));
    }
    @Test
    public void listGamesNegative() {
        //try listing games without authorization
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("hi"));
    }

    @Test
    public void joinGamePositive() throws ResponseException {
        facade.register("hehe", "haha", "hoho");
        AuthtokenData authtokenData = facade.login("hehe", "haha");
        facade.createGame("hello", authtokenData.authToken);
        Assertions.assertNull(facade.joinGame(2,"white", authtokenData.authToken));
    }
    @Test
    public void joinGameNegative() {
        //try joining games without authorization
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(1, "white", "hi"));
    }

}
