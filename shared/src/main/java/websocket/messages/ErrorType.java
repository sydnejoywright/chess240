package websocket.messages;

public record ErrorType(ServerMessage.ServerMessageType serverMessageType, String errorMessage) {
}
