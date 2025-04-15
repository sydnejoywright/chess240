package ui;

import chess.*;
import exception.ResponseException;
import model.AuthtokenData;
import model.GameData;
import websocket.WebSocketClient;
//import websocket.WebSocketFacade;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.*;


import static ui.EscapeSequences.GREEN;

public class GamePlayUI {
    private State state = State.LOGGEDIN;
    private String authToken;
    private String username;
    private int gameID;
    private String gameName;
//    private WebSocketFacade client;
    private HashMap<Integer, Integer> gameRefs;
    private boolean isPlayer;
    String color;
    private WebSocketClient client;
    private ChessGame.TeamColor asTeam;
    private String serverUrl;
    private static final List<String> LETTERS = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");

    public GamePlayUI(String username, String authToken, GameData gameData, ChessGame.TeamColor asTeam,
                      Boolean isPlayer, int gameID, String color) throws ResponseException, IOException {
        this.authToken = authToken;
        this.username = username;
        this.asTeam = asTeam;
        this.color = color;
        this.gameName = gameData.getGameName();
        this.serverUrl = "http://localhost:8080";
        this.client = new WebSocketClient(serverUrl, asTeam, gameData, username, this.gameName, color);
        this.gameID = gameID;
        client.sendMessage(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                authToken, gameID)));
        this.isPlayer = isPlayer;
    }

    public String run() {
        System.out.print(help());
//        try{redrawBoard();} catch (Exception e) {}
        Scanner scannerz = new Scanner(System.in);
        var end = "";
        while (!end.equals("quit")) {
            printPrompt();
            String liney = scannerz.nextLine();

            try {
                end = eval(liney);
                System.out.print(EscapeSequences.BLUE + end);
            } catch (Throwable q) {
                var msg = q.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
        return end;
    }
    private void removePrompt() {
        System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
        for (int i = 0; i < username.length() + gameName.length() + asTeam.toString().length(); i++) {
            System.out.print("\b");
        }
    }
    private void printPrompt() {
        removePrompt();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.BLUE + "["
                + EscapeSequences.GREEN + username + EscapeSequences.BLUE + ": in game "
                + EscapeSequences.GREEN + gameName + EscapeSequences.BLUE + " as "
                + EscapeSequences.GREEN + color + EscapeSequences.BLUE + "] >>> " + GREEN);
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
            System.out.println("Problem: " + ex.getMessage());
        } catch (IOException e) {
            return e.getMessage();
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        return "";
    }


    public String redrawBoard () throws ResponseException{
        removePrompt();
        client.redraw(asTeam);
        return "";
    };

    public String leaveGame () throws ResponseException, IOException {
        try {
            client.sendMessage(new Gson().toJson(new LeaveCommand(UserGameCommand.CommandType.LEAVE,
                    new AuthtokenData(username, authToken), gameID, asTeam)));
            return "Leaving Game...\n" + new LoggedIn(serverUrl, authToken, username).run();
        } catch (Exception e) {
            System.out.println("Exception caught in sendMessage websocketClient: " + e.getMessage());
        }
        return "";
    };

    private void executeMove(GameData currentData, ChessGame currentGame, ChessMove move) throws InvalidMoveException {
        currentGame.makeMove(move);
        currentData.setChessGame(currentGame);
        try {
            client.sendMessage(new Gson().toJson(new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE,
                    move, gameID, authToken, currentData)));
        } catch (IOException e) {
            System.out.println("Exception caught in sendMessage websocketClient: " + e.getMessage()
                    + "request came from makeMove gameplay");
        }
    }

    public String makeMove(String... params) throws ResponseException, IOException, InvalidMoveException {
        GameData currentGameData = client.getCurrentGameData();
        ChessGame currentGame = currentGameData.getChessGame();

        if (currentGameData.getChessGame().isInStalemate(ChessGame.TeamColor.WHITE)){ return EscapeSequences.RED
                + "Stalemate: game is over\n" + EscapeSequences.RESET_TEXT_COLOR; }
        else if (currentGame.isInCheckmate(ChessGame.TeamColor.WHITE)){ return EscapeSequences.RED
                + currentGameData.getWhiteUsername() + " is in checkmate: game is over\n"
                + EscapeSequences.RESET_TEXT_COLOR; }
        else if (currentGame.isInCheckmate(ChessGame.TeamColor.BLACK)){ return EscapeSequences.RED
                + currentGameData.getBlackUsername() + " is in checkmate: game is over\n"
                + EscapeSequences.RESET_TEXT_COLOR; }

        if(currentGame.getTeamTurn()!=asTeam){
            return EscapeSequences.RED + "It's not your turn\n" + EscapeSequences.RESET_TEXT_COLOR;
        }
        if(params.length == 2) {
            //did the user give me two valid moves ie letter followed by number?
            String col1 = String.valueOf(params[0].charAt(0)).toLowerCase();
            String col2 = String.valueOf(params[1].charAt(0)).toLowerCase();

            if (!LETTERS.contains(col1) || !Character.isDigit(params[0].charAt(1))) {
                return EscapeSequences.RED
                        + "Both of your parameters need to be in the form [letter <a-h>][number <1-8>]\n"
                        + EscapeSequences.RESET_TEXT_COLOR;
            } else if (!LETTERS.contains(col2) || !Character.isDigit(params[1].charAt(1))) {
                return EscapeSequences.RED
                        + "Both of your parameters need to be in the form [letter <a-h>][number <1-8>]\n"
                        + EscapeSequences.RESET_TEXT_COLOR;
            }

            //create a chess move, which requires changing letters to numbers.
            int from = convertLetters(col1);
            int to = convertLetters(col2);

            ChessPosition start = new ChessPosition(Integer.parseInt(String.valueOf(params[0].charAt(1))), from);
            ChessPosition end = new ChessPosition(Integer.parseInt(String.valueOf(params[1].charAt(1))), to);
            ChessMove move = new ChessMove(start, end, null);

            ChessPiece piece = currentGame.getBoard().getPiece(start);
            //if they try to move a piece that isn't there
            if(piece == null){
                return EscapeSequences.RED + "There is no piece at that position, check your parameters and try again\n"
                        + EscapeSequences.RESET_TEXT_COLOR;
            }
            //check if the piece they are trying to move is their own
            if(!(piece.getTeamColor() == asTeam)){
                return EscapeSequences.RED + "That piece doesn't belong to you\n" + EscapeSequences.RESET_TEXT_COLOR;
            }
            else {
                // check if it's a pawn, and if so it will need promotion
                if ((piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) &&
                        ((asTeam == ChessGame.TeamColor.WHITE && start.getRow() == 7) ||
                                (asTeam == ChessGame.TeamColor.BLACK && start.getRow() == 2))) {
                    boolean selectedPiece = false;
                    while (!selectedPiece) {
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA
                                + "\tWhat piece do you want to promote to?\n\t" + EscapeSequences.RESET_TEXT_COLOR);
                        Scanner scanner = new Scanner(System.in);
                        String line = scanner.nextLine();
                        var tokens = line.toLowerCase().split(" ");
                        if (tokens.length > 1) {
                            System.out.println(EscapeSequences.RED
                                    + "\tYour piece should be one word. Here are your options: " +
                                    "\n\tQUEEN, KNIGHT, ROOK, BISHOP" + EscapeSequences.RESET_TEXT_COLOR);
                        } else {
                            switch (tokens[0]) {
                                case "bishop" ->
                                { executeMove(currentGameData, currentGame, new ChessMove(move.getStartPosition(),
                                        move.getEndPosition(), ChessPiece.PieceType.BISHOP));
                                    selectedPiece = true; break; }
                                case "queen" ->
                                { executeMove(currentGameData, currentGame, new ChessMove(move.getStartPosition(),
                                        move.getEndPosition(), ChessPiece.PieceType.QUEEN));
                                    selectedPiece = true; break; }
                                case "rook" ->
                                { executeMove(currentGameData, currentGame, new ChessMove(move.getStartPosition(),
                                        move.getEndPosition(), ChessPiece.PieceType.ROOK));
                                    selectedPiece = true; break; }
                                case "knight" ->
                                { executeMove(currentGameData, currentGame, new ChessMove(move.getStartPosition(),
                                        move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                                    selectedPiece = true; break; }
                                case null, default ->
                                        System.out.println(EscapeSequences.RED +
                                                "\tThat piece isn't valid. Here are your options: " +
                                                "\n\tQUEEN, KNIGHT, ROOK, BISHOP" + EscapeSequences.RESET_TEXT_COLOR);
                            }
                        }
                    }

                }
                // if it's not a pawn
                Collection<ChessMove> validMoves = currentGame.validMoves(start);
                boolean moveHappened = false;
                for (ChessMove currMove : validMoves) {
                    if (moveHappened) { continue; }
                    if (currMove.equals(move)) {
                        executeMove(currentGameData, currentGame, move);
                        moveHappened = true;
                    }
                }
                if (!moveHappened){
//                        System.out.println(moves);
//                        System.out.println(move);
                    if (!validMoves.contains(move) && piece.pieceMoves(currentGame.getBoard(), start).contains(move)){
                        return EscapeSequences.RED + "That move leaves or places your king in check!\n"
                                + EscapeSequences.RESET_TEXT_COLOR;}
                    return EscapeSequences.RED + "That's not a valid move for that piece\n"
                            + EscapeSequences.RESET_TEXT_COLOR;
                }
                if (currentGameData.getChessGame().isInStalemate(ChessGame.TeamColor.WHITE))
                {removePrompt(); System.out.println(EscapeSequences.GREEN + "Stalemate. Game is over."
                        + EscapeSequences.RESET_TEXT_COLOR); printPrompt(); }
                else if (currentGameData.getChessGame().isInCheckmate(ChessGame.TeamColor.WHITE))
                {removePrompt(); System.out.println(EscapeSequences.GREEN + "Checkmate. You won."
                        + EscapeSequences.RESET_TEXT_COLOR); printPrompt(); }
                else if (currentGameData.getChessGame().isInCheckmate(ChessGame.TeamColor.BLACK))
                {removePrompt(); System.out.println(EscapeSequences.GREEN + "Checkmate. You won."
                        + EscapeSequences.RESET_TEXT_COLOR); printPrompt(); }
                else if (currentGameData.getChessGame().isInCheck(ChessGame.TeamColor.WHITE))
                {removePrompt(); System.out.println(EscapeSequences.GREEN + "You put the other team in check"
                        + EscapeSequences.RESET_TEXT_COLOR); printPrompt(); }
                else if (currentGameData.getChessGame().isInCheck(ChessGame.TeamColor.BLACK))
                {removePrompt(); System.out.println(EscapeSequences.GREEN + "You put the other team in check"
                        + EscapeSequences.RESET_TEXT_COLOR); printPrompt(); }
            }
            return "";
        }
        return EscapeSequences.RED + "Check your parameters: Expected <start position> <end position>\n"
            + EscapeSequences.RESET_TEXT_COLOR;
    };
    public String resignGame() throws ResponseException{
        GameData currentGameData = client.getCurrentGameData();
        ChessGame currentGame = currentGameData.getChessGame();
        if (currentGame.isFinished()) {
            return "You cannot resign. The game is already finished";
        }
        boolean doIt = false;
        while (!doIt) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA + "\tAre you sure you want to resign (y/n)?\n\t"
                    + EscapeSequences.RESET_TEXT_COLOR);
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var tokens = line.toLowerCase().split(" ");
            if (tokens.length > 1) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA
                        + "\tAre you sure you want to resign (y/n)?\n\n"
                        + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                switch (tokens[0]) {
                    case "y" -> {doIt = true;}
                    case "n" -> {return "";}
                    case null, default ->
                            System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA
                                    + "\tAre you sure you want to resign (y/n)?\n\n"
                                    + EscapeSequences.RESET_TEXT_COLOR);
                }
            }
        }
        try {
            client.sendMessage(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                    authToken, gameID)));
        } catch (IOException e) {
            System.out.println("Exception caught in sendMessage websocketClient: " + e.getMessage()
                    + "request came from resignGame gameplay");
        }
        return "";

    };
    public String highlightMoves(String... params) throws ResponseException{
       if(params.length == 1){
           String col1 = String.valueOf(params[0].charAt(0)).toLowerCase();
           if (!LETTERS.contains(col1) || !Character.isDigit(params[0].charAt(1))) {
               return EscapeSequences.RED +
                       "Both of your parameters need to be in the form [letter <a-h>][number <1-8>]\n"
                       + EscapeSequences.RESET_TEXT_COLOR;
           }
           int from = convertLetters(col1);
           ChessPosition start = new ChessPosition(Integer.parseInt(String.valueOf(params[0].charAt(1))), from);
           GameData currentGameData = client.getCurrentGameData();
           if (currentGameData.getChessGame().getBoard().getPiece(start) == null) {
               return EscapeSequences.RED + "There is no piece on that space\n" + EscapeSequences.RESET_TEXT_COLOR;
           }
           ChessBoardUI.highlightGame(currentGameData.getChessGame(), asTeam, start);
           return "";
       }
       return EscapeSequences.RED + "Check your parameters: Expected <position>\n" + EscapeSequences.RESET_TEXT_COLOR;
    };


    public int convertLetters(String letter) {
        return switch (letter) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> -1;  // return -1 to signal an invalid input
        };
    }

    public String help() {
        if(isPlayer) {
            return EscapeSequences.SET_TEXT_COLOR_YELLOW +"""
                    - redraw - redraws board
                    - leave
                    - move
                    - resign
                    - highlight
                    - quit - playing chess
                    - help - with possible commands
                    """;
        }else{
            return EscapeSequences.SET_TEXT_COLOR_YELLOW +"""
                    - redraw - redraws board
                    - leave
                    - highlight
                    - quit - playing chess
                    - help - with possible commands
                    """;
        }
    }

}

