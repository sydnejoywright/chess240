import chess.*;
import ui.LoggedOut;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var serverURL = "http://localhost:8081";
        LoggedOut newSession = new LoggedOut(serverURL);
        newSession.run();
    }
}