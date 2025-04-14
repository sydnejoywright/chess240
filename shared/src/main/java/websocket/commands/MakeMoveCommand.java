package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;

public record MakeMoveCommand(UserGameCommand.CommandType commandType, ChessMove move, int gameID, String authToken, GameData currentGameData) {
}
