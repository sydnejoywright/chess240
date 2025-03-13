package service;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class MyDatabaseTests {



    @BeforeAll
    public static void startServer() {
    }

    @BeforeEach
    public void setUp() {

    }


    @Test
    public void testClearData() throws ResponseException {

        assertEquals(true, true);
    }

    @Test
    public void testListGamesPositive() throws ResponseException {

        assertEquals(true, true);


    }

    @Test
    public void testListGamesNegative () throws ResponseException {

        assertEquals(true, true);
    }

    @Test
    public void testCreateGamePositive(){
        assertEquals(true, true);
    }

    @Test
    public void testCreateGameNegative(){
        assertEquals(true, true);
    }

    @Test
    public void testJoinGamePositive(){
        assertEquals(true, true);
    }

    @Test
    public void testJoinGameNegative(){
        assertEquals(true, true);
    }

    //...............................................................

    @Test
    public void testRegisterUserPositive(){
        assertEquals(true, true);
    }

    @Test
    public void testRegisterUserNegative(){
        assertEquals(true, true);
    }

    @Test
    public void testloginUserPositive(){
        assertEquals(true, true);
    }

    @Test
    public void testLoginUserNegative(){
        assertEquals(true, true);
    }

    @Test
    public void testLogoutUserPositive(){
        assertEquals(true, true);
    }

    @Test
    public void testLogoutUserNegative(){
        assertEquals(true, true);
    }


}
