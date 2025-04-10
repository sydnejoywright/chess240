package websocket.messages;

public record ErrorType(ServerMessage.ServerMessageType messageType, String errorMessage) {
}
