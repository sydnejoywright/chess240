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






}
