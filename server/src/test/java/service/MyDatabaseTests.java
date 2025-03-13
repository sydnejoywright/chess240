package service;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class MyDatabaseTests {



    @BeforeAll
    public static void startServer() {
    }

    @BeforeEach
    public void setUp() {

    }

    @AfterAll
    static void stopServer() {

    }






}
