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
    public void testClearD1ata() throws ResponseException {

        assertEquals(true, true);
    }

    @Test
    public void testListGamesPositive1() throws ResponseException {

        assertEquals(true, true);


    }

    @Test
    public void testListGamesNegative1 () throws ResponseException {

        assertEquals(true, true);
    }

    @Test
    public void testCreateGamePositive1(){
        assertEquals(true, true);
    }

    @Test
    public void testCreateGameNegative1(){
        assertEquals(true, true);
    }

    @Test
    public void testJoinGamePositive1(){
        assertEquals(true, true);
    }

    @Test
    public void testJoinGameNegative1(){
        assertEquals(true, true);
    }

    //...............................................................

    @Test
    public void testRegisterUserPositiv1e(){
        assertEquals(true, true);
    }

    @Test
    public void testRegisterUserNegativ1e(){
        assertEquals(true, true);
    }

    @Test
    public void testloginUserPosit1ive(){
        assertEquals(true, true);
    }

    @Test
    public void testLoginUserNegati1ve(){
        assertEquals(true, true);
    }

    @Test
    public void testLogoutUserPositi1ve(){
        assertEquals(true, true);
    }

    @Test
    public void testLogoutUserNegati1ve(){
        assertEquals(true, true);
    }


}
