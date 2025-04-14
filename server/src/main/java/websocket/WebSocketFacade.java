package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthtokenData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameType;
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
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        System.out.println("Message from " + activeSessions.get(session) + ": " + message);
        var command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT:
                connect(command.getAuthToken(), command.getGameID(), session);
                break;
            case MAKE_MOVE:
                var move = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(move.authToken(), move.gameID(), move.move(), move.currentGameData(), session);
                break;
            case LEAVE:
                var leavecom = new Gson().fromJson(message, LeaveCommand.class);
                leave(leavecom.authtokenData(), leavecom.gameID(), leavecom.asTeam(), session);
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

    private void updateAllUserGames(GameData gameData, int gameID){
        String message = new Gson().toJson(new LoadGameType(ServerMessage.ServerMessageType.LOAD_GAME, gameData));
        for(HashMap.Entry<Session, SessionInfo> sesh : activeSessions.entrySet()){
//            System.out.println(sesh.getValue().gameID());
//            System.out.println(gameID);
            if(sesh.getValue().gameID() == gameID){
                try {
                    System.out.println("anything");
                    sesh.getKey().getRemote().sendString(message);
                    System.out.println("should have sent message by now");
                } catch (IOException e) {
                    System.out.println("Error in updateallusergames: " + e.getMessage());
                }
            }
        }
    }

    private void notifyOthers(String msg, int gameID, Session dontNotify){
        String message = new Gson().toJson(new NotificationType(ServerMessage.ServerMessageType.NOTIFICATION, msg));
        for(HashMap.Entry<Session, SessionInfo> sesh : activeSessions.entrySet()){
            if(sesh.getKey() != dontNotify) {
                if (sesh.getValue().gameID() == gameID) {
                    try {
                        sesh.getKey().getRemote().sendString(message);
                    } catch (IOException e) {
                        System.out.println("Error in broadcast: " + e.getMessage());
                    }
                }
            }
        }
    }


    private void connect(String authToken, int gameID, Session session){
        SessionInfo info = new SessionInfo(gameID, authToken);
        activeSessions.put(session, info);
    }

    private void makeMove(String authToken, int gameID, ChessMove move, GameData currentGameData, Session session) throws DataAccessException {
        gameDAO.updateGame(currentGameData);
        AuthtokenData authtokenData = new AuthtokenData("", authToken);
        AuthtokenData data = authDAO.getAuth(authtokenData);
        notifyOthers(data.username + " made a move from " + convert_numbers(move.getStartPosition().getColumn()) + move.getStartPosition().getRow() + " to " + convert_numbers(move.getEndPosition().getColumn()) + move.getEndPosition().getRow(), gameID, session);
        // adding logic for checkmate, mate, check
        if (currentGameData.getChessGame().isInStalemate(ChessGame.TeamColor.WHITE)){notifyOthers("Stalemate: game is over", gameID, session);}
        else if (currentGameData.getChessGame().isInCheckmate(ChessGame.TeamColor.WHITE)){notifyOthers(currentGameData.getWhiteUsername() + " is in checkmate: game is over", gameID, session);}
        else if (currentGameData.getChessGame().isInCheckmate(ChessGame.TeamColor.BLACK)){notifyOthers(currentGameData.getBlackUsername() + " is in checkmate: game is over", gameID, session);}
        else if (currentGameData.getChessGame().isInCheck(ChessGame.TeamColor.WHITE)){notifyOthers(currentGameData.getWhiteUsername() + " is in check", gameID, session);}
        else if (currentGameData.getChessGame().isInCheck(ChessGame.TeamColor.BLACK)){ notifyOthers(currentGameData.getBlackUsername() + " is in check", gameID, session);}
        updateAllUserGames(currentGameData, gameID);
    }


    private void leave(AuthtokenData authtokenData, int gameID, ChessGame.TeamColor asTeam, Session session) throws IOException, DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        activeSessions.remove(session);
        if(asTeam == ChessGame.TeamColor.WHITE){
            gameData.setWhiteUsername(null);
            gameDAO.updateGame(gameData);
        }
        else{
            gameData.setBlackUsername(null);
            gameDAO.updateGame(gameData);
        }
        notifyOthers(authtokenData.username + " has left the game", gameID, session);
    }

    private void resign(String authToken, int gameID, Session session){}

    public String convert_numbers(int num) {
        return switch (num) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> "err";  // return -1 to signal an invalid input
        };
    }
}