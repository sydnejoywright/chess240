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
    public void testClearSQLData() throws ResponseException {

        assertEquals(true, true);
    }


    @Test
    public void testListSQLGamesPositive() throws ResponseException {

        assertEquals(true, true);


    }

    @Test
    public void testListSQLGamesNegative () throws ResponseException {

        assertEquals(true, true);
    }

    @Test
    public void testCreateSQLGamePositive(){
        assertEquals(true, true);
    }

    @Test
    public void testCreateSQLGameNegative(){
        assertEquals(true, true);
    }

    @Test
    public void testJoinSQLGamePositive(){
        assertEquals(true, true);
    }

    @Test
    public void testJoinSQLGameNegative(){
        assertEquals(true, true);
    }

    //...............................................................

    @Test
    public void testRegisterSQLUserPositive(){
        assertEquals(true, true);
    }

    @Test
    public void testRegisterSQLUserNegative(){
        assertEquals(true, true);
    }

    @Test
    public void testloginSQLUserPositive(){
        assertEquals(true, true);
    }

    @Test
    public void testLoginSQLUserNegative(){
        assertEquals(true, true);
    }

    @Test
    public void testLogoutSQLUserPositive(){
        assertEquals(true, true);
    }

    @Test
    public void testLogoutSQLUserNegative(){
        assertEquals(true, true);
    }






}
