package websocket.messages;

import chess.ChessGame;

public record LoadGameType(ServerMessage.ServerMessageType messageType, ChessGame game) {
}
