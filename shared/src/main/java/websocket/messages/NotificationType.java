package websocket.messages;

public record NotificationType (ServerMessage.ServerMessageType serverMessageType, String message) {

}
