package websocket.commands;

import chess.ChessMove;
import chess.ChessPosition;

public record MakeMoveCommand(UserGameCommand.CommandType commandType, ChessMove move, int gameID, String authToken) {
}
