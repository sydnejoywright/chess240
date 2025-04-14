package websocket.commands;

import chess.ChessGame;
import model.AuthtokenData;

public record LeaveCommand(UserGameCommand.CommandType commandType, AuthtokenData authtokenData, int gameID, ChessGame.TeamColor asTeam) {
}
