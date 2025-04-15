package websocket;

import model.GameData;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import exception.ResponseException;
import com.google.gson.Gson;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import ui.ChessBoardUI;
import ui.EscapeSequences;
import websocket.messages.NotificationType;
import websocket.messages.ErrorType;
import websocket.messages.LoadGameType;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.GREEN;

//need to extend Endpoint for websocket to work properly
@ClientEndpoint
public class WebSocketClient extends Endpoint {

    Session session;
    GameData currentGameData;
    ChessGame.TeamColor asTeam;
    String username;
    String gameName;
    String name;

    public WebSocketClient(String url, ChessGame.TeamColor asTeam, GameData currentGameData, String username,
                           String gameName, String name) throws ResponseException {
        this.currentGameData = currentGameData;
        this.asTeam = asTeam;
        this.username = username;
        this.gameName = gameName;
        this.name = name;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
//            System.out.println("Socket URI: " + socketURI);

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

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    private void removePrompt() {
        System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
        for (int i = 0; i < username.length() + gameName.length() + asTeam.toString().length(); i++) {
            System.out.print("\b");
        }
    }
    private void printPromptyPoo() {
        removePrompt();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.BLUE + "["
                + EscapeSequences.GREEN + username + EscapeSequences.BLUE +
                ": in game " + EscapeSequences.GREEN + gameName + EscapeSequences.BLUE +
                " as " + EscapeSequences.GREEN + name + EscapeSequences.BLUE + "] >>> " + GREEN);
    }

    public void redraw(ChessGame.TeamColor asTeam){
        removePrompt();
        ChessBoardUI.displayGame(currentGameData.getChessGame(), asTeam);
        printPromptyPoo();
    }

    public void sendMessage(String command) throws IOException {
        try {
//            System.out.println("Received commmand about to print it");
//            System.out.println(command);
            session.getBasicRemote().sendText(command);

        } catch (IOException e) {
            System.out.println("Exception caught in sendMessage websocketClient: " + e.getMessage());
        }
    }


    private void handle(String string){
        ServerMessage msg = new Gson().fromJson(string, ServerMessage.class);
//        System.out.println(string);
        switch(msg.getServerMessageType()){
            case LOAD_GAME:
                LoadGameType loadGameType = new Gson().fromJson(string, LoadGameType.class);
//                System.out.println("UPDATING GAME FOR " + username);
                currentGameData = loadGameType.game();
//                System.out.println("received update game in websocketclient");
                removePrompt();
                ChessBoardUI.displayGame(currentGameData.getChessGame(), asTeam);
                System.out.print(EscapeSequences.RESET_TEXT_COLOR);
                printPromptyPoo();
                break;
            case ERROR:
                removePrompt();
                ErrorType errorType = new Gson().fromJson(string, ErrorType.class);
                System.out.println(EscapeSequences.RED + errorType.errorMessage() + EscapeSequences.RESET_TEXT_COLOR);
                printPromptyPoo();
                break;
            case NOTIFICATION:
                removePrompt();
                NotificationType notificationType = new Gson().fromJson(string, NotificationType.class);
                System.out.println(notificationType.message() + EscapeSequences.RESET_TEXT_COLOR);
                printPromptyPoo();
                break;
        }
    }

    public GameData getCurrentGameData(){
        return currentGameData;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        currentGameData.getChessGame().makeMove(move);
    }
}