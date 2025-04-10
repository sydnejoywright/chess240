package websocket;

import chess.ChessGame;
import exception.ResponseException;
import com.google.gson.Gson;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import ui.ChessBoardUI;
import websocket.commands.*;
import websocket.messages.NotificationType;
import websocket.messages.ErrorType;
import websocket.messages.LoadGameType;
import websocket.messages.ServerMessage;

//need to extend Endpoint for websocket to work properly
public class WebSocketClient extends Endpoint {

    Session session;
    ChessGame currentGame;
    ChessGame.TeamColor asTeam;

    public WebSocketClient(String url, ChessGame currentGame, ChessGame.TeamColor asTeam) throws ResponseException {
        this.currentGame = currentGame;
        this.asTeam = asTeam;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handle(message);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.println("Problem in websocket facade: ");
            System.out.println(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("onOpen was called from WebSocketClient");
    }

    private void handle(String string){
        ServerMessage msg = new Gson().fromJson(string, ServerMessage.class);
        switch(msg.getServerMessageType()){
            case LOAD_GAME:
                LoadGameType loadGameType = new Gson().fromJson(string, LoadGameType.class);
                currentGame = loadGameType.game();
                ChessBoardUI.displayGame(currentGame, asTeam);
                break;
            case ERROR:
                ErrorType errorType = new Gson().fromJson(string, ErrorType.class);
                System.out.println(errorType.errorMessage());
                break;
            case NOTIFICATION:
                NotificationType notificationType = new Gson().fromJson(string, NotificationType.class);
                System.out.println(notificationType.message());
                break;
        }
    }
}