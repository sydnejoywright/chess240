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
import websocket.messages.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

//need to extend Endpoint for websocket to work properly
@WebSocket
public class WebSocketFacade {
    GameDAO gameDAO;
    AuthDAO authDAO;
    UserDAO userDAO;

    private HashMap<Session, SessionInfo> activeSessions = new HashMap<>();


    public WebSocketFacade(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) throws ResponseException {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;

    }

    //Endpoint requires this method, but you don't have to do anything
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
//        System.out.println("CONNECTED: " + session.getRemote());
    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String message){
        activeSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
//        System.out.println("Message from " + session + ": " + message);
        var command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT:
                System.out.println("CONNECT");
                connect(command.getAuthToken(), command.getGameID(), session);
                break;
            case MAKE_MOVE:
                System.out.println("MAKE_MOVE");
                var move = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(move.authToken(), move.gameID(), move.move(), move.currentGameData(), session);
                break;
            case LEAVE:
                System.out.println("LEAVE");
                leave(command.getAuthToken(), command.getGameID(), session);
                break;
            case RESIGN:
                System.out.println("RESIGN");
                resign(command.getAuthToken(), command.getGameID(), session);
                break;
        }
    }

    private void broadcastMessage(String msg, int gameID) throws IOException {
        String message = new Gson().toJson(new NotificationType(NOTIFICATION, msg));
        for(HashMap.Entry<Session, SessionInfo> sesh : activeSessions.entrySet()){
//            System.out.println(sesh.getValue().gameID());
//            System.out.println(gameID);
            if(sesh.getValue().gameID() == gameID){
                try {
                    sesh.getKey().getRemote().sendString(message);
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
                    sesh.getKey().getRemote().sendString(message);
                } catch (IOException e) {
                    System.out.println("Error in updateallusergames: " + e.getMessage());
                }
            }
        }
    }

    private void notifyOthers(String msg, int gameID, Session dontNotify){
        String message = new Gson().toJson(new NotificationType(NOTIFICATION, msg));
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
        AuthtokenData authData = null;
        try {
            GameData gameData = gameDAO.getGame(gameID);
            authData = authDAO.getAuth(new AuthtokenData(null, authToken));
            if (gameData == null) {
                session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Bad GameID")));
                return;
            } else if (authData == null) {
                session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Bad authToken")));
                return;
            }
            String message = new Gson().toJson(new LoadGameType(ServerMessage.ServerMessageType.LOAD_GAME, gameData));
            session.getRemote().sendString(message);
            System.out.println("updating game");
            notifyOthers(authData.username + " has joined the game.", gameID, session);
            activeSessions.put(session, info);


        } catch (Exception e) {
            if (authData == null) {
                try { session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Bad authToken"))); }
                catch (Exception exception) {}
            return;
        }System.out.println("exception thrown in connect() in WebSocketFacade()"); }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, GameData currentGameData, Session session) throws DataAccessException {
//        gameDAO.updateGame(currentGameData);
        GameData gameData = gameDAO.getGame(gameID);
        AuthtokenData authData = null;
        authData = authDAO.getAuth(new AuthtokenData(null, authToken));
        if (authData == null) {
            try {
                session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Bad authToken")));
            } catch (Exception exception) {}
            return;
        }

        if (gameData.getChessGame().isFinished()) {
            try {
                session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Cannot make moves, the game is over")));
            } catch (Exception exception) {}
            return;
        }

        if ((gameData.getChessGame().getTeamTurn() == ChessGame.TeamColor.BLACK && !authData.username.equals(gameData.getBlackUsername()))
           || gameData.getChessGame().getTeamTurn() == ChessGame.TeamColor.WHITE && !authData.username.equals(gameData.getWhiteUsername()) ) {
            try {
                session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Not your turn")));
            } catch (Exception exception) {}
            return;
        }
        try { gameData.getChessGame().makeMove(move); }
        catch(Exception exc) {
            try { session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Illegal move"))); }
            catch (Exception exception) {}
            return;
        }
        gameDAO.updateGame (gameData);
        currentGameData = gameData;
        AuthtokenData authtokenData = new AuthtokenData("", authToken);
        AuthtokenData data = authDAO.getAuth(authtokenData);

        // adding logic for checkmate, mate, check
        if (currentGameData.getChessGame().isInStalemate(ChessGame.TeamColor.WHITE)){notifyOthers(data.username + " made a move from " + convert_numbers(move.getStartPosition().getColumn()) + move.getStartPosition().getRow() + " to " + convert_numbers(move.getEndPosition().getColumn()) + move.getEndPosition().getRow() + "\nStalemate: game is over", gameID, session);}
        else if (currentGameData.getChessGame().isInCheckmate(ChessGame.TeamColor.WHITE)){notifyOthers(data.username + " made a move from " + convert_numbers(move.getStartPosition().getColumn()) + move.getStartPosition().getRow() + " to " + convert_numbers(move.getEndPosition().getColumn()) + move.getEndPosition().getRow() + "\n" + currentGameData.getWhiteUsername() + " is in checkmate: game is over", gameID, session);}
        else if (currentGameData.getChessGame().isInCheckmate(ChessGame.TeamColor.BLACK)){notifyOthers(data.username + " made a move from " + convert_numbers(move.getStartPosition().getColumn()) + move.getStartPosition().getRow() + " to " + convert_numbers(move.getEndPosition().getColumn()) + move.getEndPosition().getRow() + "\n" + currentGameData.getBlackUsername() + " is in checkmate: game is over", gameID, session);}
        else if (currentGameData.getChessGame().isInCheck(ChessGame.TeamColor.WHITE)){notifyOthers(data.username + " made a move from " + convert_numbers(move.getStartPosition().getColumn()) + move.getStartPosition().getRow() + " to " + convert_numbers(move.getEndPosition().getColumn()) + move.getEndPosition().getRow() + "\n" + currentGameData.getWhiteUsername() + " is in check", gameID, session);}
        else if (currentGameData.getChessGame().isInCheck(ChessGame.TeamColor.BLACK)){ notifyOthers(data.username + " made a move from " + convert_numbers(move.getStartPosition().getColumn()) + move.getStartPosition().getRow() + " to " + convert_numbers(move.getEndPosition().getColumn()) + move.getEndPosition().getRow() + "\n" + currentGameData.getBlackUsername() + " is in check", gameID, session);}
        else notifyOthers(data.username + " made a move from " + convert_numbers(move.getStartPosition().getColumn()) + move.getStartPosition().getRow() + " to " + convert_numbers(move.getEndPosition().getColumn()) + move.getEndPosition().getRow(), gameID, session);

        updateAllUserGames(currentGameData, gameID);
    }


    private void leave(String authtoken, int gameID, Session session) throws IOException, DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        AuthtokenData authtokenData = authDAO.getAuth(new AuthtokenData(null, authtoken));
        ChessGame.TeamColor asTeam = null;
        if (gameData.getBlackUsername() != null && gameData.getBlackUsername().equals(authtokenData.username))
            asTeam = ChessGame.TeamColor.BLACK;
        else if (gameData.getWhiteUsername() != null && gameData.getWhiteUsername().equals(authtokenData.username))
            asTeam = ChessGame.TeamColor.WHITE;
        activeSessions.remove(session);
        if(asTeam == ChessGame.TeamColor.WHITE){
            gameData.setWhiteUsername(null);
            gameDAO.updateGame(gameData);
        }
        else if (asTeam != null){
            gameData.setBlackUsername(null);
            gameDAO.updateGame(gameData);
        }
        AuthtokenData another = authDAO.getAuth(authtokenData);
        broadcastMessage(another.username + " has left the game", gameID);
    }

    private void resign(String authToken, int gameID, Session session){
        AuthtokenData authData = null;
        try {
        authData = authDAO.getAuth(new AuthtokenData(null, authToken));
            if (authData == null) {
                try {
                    session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Bad authToken")));
                } catch (Exception exception) {}
                return;
            }
            if (gameDAO.getGame(gameID).getChessGame().isFinished()) {
                try {
                    session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Cannot resign, the game is over")));
                } catch (Exception exception) {}
                return;
            }
            GameData gameData = gameDAO.getGame(gameID);
            if (!authData.username.equals(gameData.getWhiteUsername()) && !authData.username.equals(gameData.getBlackUsername())){
                try {
                    session.getRemote().sendString(new Gson().toJson(new ErrorType(ServerMessage.ServerMessageType.ERROR, "Can't resign as observer")));
                } catch (Exception exception) {}
                return;
            }
            gameData.getChessGame().resign();
            gameDAO.updateGame(gameData);
            broadcastMessage(authData.username + " has resigned from the game", gameID);
        } catch (Exception exception) { System.out.println("Error in resign() in WebSocketFacade.java"); }
    }

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