package websocket.commands;

import chess.ChessGame;
import model.AuthtokenData;

public record LeaveCommand(UserGameCommand.CommandType commandType, AuthtokenData authToken, int gameID, ChessGame.TeamColor asTeam) {
}
