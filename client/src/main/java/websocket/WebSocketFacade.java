//package websocket;
//
//import com.google.gson.Gson;
//import exception.ResponseException;
//import websocket.messages.ServerMessage;
//
//import javax.websocket.*;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//
////need to extend Endpoint for websocket to work properly
//public class WebSocketFacade extends Endpoint {
//
//    Session session;
//
//    public WebSocketFacade(String url) throws ResponseException {
//        try {
//            url = url.replace("http", "ws");
//            URI socketURI = new URI(url + "/ws");
//
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            this.session = container.connectToServer(this, socketURI);
//
//        } catch (DeploymentException | IOException | URISyntaxException ex) {
//            System.out.println("Problem in websocket facade: ");
//            System.out.println(ex.getMessage());
//        }
//    }
//
//    //Endpoint requires this method, but you don't have to do anything
//    @Override
//    public void onOpen(Session session, EndpointConfig endpointConfig) {
//    }
//
//    public void enterPetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.ENTER, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//    public void leavePetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//}