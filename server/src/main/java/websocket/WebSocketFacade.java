package websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import websocket.messages.SessionInfo;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {
    GameDAO gameDAO;
    AuthDAO authDAO;
    UserDAO userDAO;
    private static HashMap<Session, SessionInfo> activeSessions = new HashMap<>();


    public WebSocketFacade(String url, AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) throws ResponseException {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
    @OnWebSocketClose
    public void onClose(Session session){
        activeSessions.remove(session);
    }

    private void handle(){}


}