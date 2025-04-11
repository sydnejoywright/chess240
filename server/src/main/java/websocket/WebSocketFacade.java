package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationType;
import websocket.messages.ServerMessage;
import websocket.messages.SessionInfo;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

//need to extend Endpoint for websocket to work properly
@WebSocket
public class WebSocketFacade {
    GameDAO gameDAO;
    AuthDAO authDAO;
    UserDAO userDAO;

    private static HashMap<Session, SessionInfo> activeSessions = new HashMap<>();


    public WebSocketFacade(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) throws ResponseException {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;

    }

    //Endpoint requires this method, but you don't have to do anything
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("CONNECTED: " + session.getRemote());
    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String message){
        activeSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Message from " + activeSessions.get(session) + ": " + message);
        var command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT:
                connect(command.getAuthToken(), command.getGameID(), session);
                break;
            case MAKE_MOVE:
                var move = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(move.authToken(), move.gameID(), move.move(), session);
                break;
            case LEAVE:
                leave(command.getAuthToken(), command.getGameID(), session);
                break;
            case RESIGN:
                resign(command.getAuthToken(), command.getGameID(), session);
                break;
        }
    }

    private void broadcastMessage(String msg, int gameID) throws IOException {
        String message = new Gson().toJson(new NotificationType(ServerMessage.ServerMessageType.NOTIFICATION, msg));
        for(HashMap.Entry<Session, SessionInfo> sesh : activeSessions.entrySet()){
            System.out.println(sesh.getValue().gameID());
            System.out.println(gameID);
            if(sesh.getValue().gameID() == gameID){
                try {
                    System.out.println("anything");
                    sesh.getKey().getRemote().sendString(message);
                    System.out.println("should have sent message by now");

                } catch (IOException e) {
                    System.out.println("Error in broadcast: " + e.getMessage());
                }
            }
        }
    }


    private void connect(String authToken, int gameID, Session session){
        SessionInfo info = new SessionInfo(gameID, authToken);
        activeSessions.put(session, info);
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session){}
    private void leave(String authToken, int gameID, Session session) throws IOException {
        System.out.println("zachs butt is juicy");
        System.out.println("GameId of the person leaving: " + gameID);
        broadcastMessage("hey everyone", gameID);
    }
    private void resign(String authToken, int gameID, Session session){}


}