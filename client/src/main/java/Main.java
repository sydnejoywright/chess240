import chess.*;
import ui.LoggedOut;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        var serverURL = "http://localhost:8080";
        LoggedOut newSession = new LoggedOut(serverURL);
        newSession.run();
    }
}