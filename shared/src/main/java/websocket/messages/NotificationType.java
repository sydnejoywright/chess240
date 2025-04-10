package websocket.messages;

public record NotificationType (ServerMessage.ServerMessageType messageType, String message) {

}
