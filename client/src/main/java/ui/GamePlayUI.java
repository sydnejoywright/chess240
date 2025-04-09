package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import ui.serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static ui.EscapeSequences.GREEN;

public class GamePlayUI {
    private State state = State.LOGGEDIN;
    private String authToken;
    private String username;
    private int gameID;

    public GamePlayUI(String username, String authToken, GameData gameData, ChessGame.TeamColor asTeam, int gameID){
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;

    }


    public String run() {
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(EscapeSequences.BLUE + result);
            } catch (Throwable f) {
                var msg = f.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
        return result;
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + "[LOGGED IN] " + username +" >>> " + GREEN);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> resignGame();
                case "highlight" -> highlightMoves(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }


    public String redrawBoard () throws ResponseException{
        return "";
    };
    public String leaveGame () throws ResponseException{
        return "";

    };
    public String makeMove(String... params) throws ResponseException{
        if(params.length == 2) {
            return "";
        }
        return EscapeSequences.RED + "Check your parameters: Expected <start position> <end position>" + EscapeSequences.RESET_TEXT_COLOR;
    };
    public String resignGame() throws ResponseException{
        return "";

    };
    public String highlightMoves(String... params) throws ResponseException{
       if(params.length == 1){
           return "";

       }
        return EscapeSequences.RED + "Check your parameters: Expected <position>" + EscapeSequences.RESET_TEXT_COLOR;
    };


    public String help() {
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE][BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
    }

}

