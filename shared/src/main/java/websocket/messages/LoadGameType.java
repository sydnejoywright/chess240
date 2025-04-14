package websocket.messages;

import chess.ChessGame;
import model.GameData;

public record LoadGameType(ServerMessage.ServerMessageType serverMessageType, GameData gameData) {
}
