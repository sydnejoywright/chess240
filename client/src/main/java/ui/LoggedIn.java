package ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import chess.ChessGame;
import ui.serverfacade.ServerFacade;
import model.*;
import exception.ResponseException;

import static ui.EscapeSequences.GREEN;

public class LoggedIn {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private HashMap<Integer, Integer> gameRefs;
    private State state = State.LOGGEDIN;
    private String authToken;
    private String username;

    public LoggedIn(String serverUrl, String authToken, String username) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.username = username;
        this.gameRefs = new HashMap<>();
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
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        if(params.length == 1) {
            try {
                assertSignedIn();
                server.createGame(params[0], authToken);
                System.out.println(EscapeSequences.GREEN + "Successfully created game" + EscapeSequences.RESET_TEXT_COLOR);
                return "";
            } catch (ResponseException e) {
                if (e.getMessage().equals("User is not logged in")) {
                    return EscapeSequences.RED + "You must log in in order to perform this action" + EscapeSequences.RESET_TEXT_COLOR;
                } else if (e.getMessage().equals("Error: bad request")) {
                    return EscapeSequences.RED + "Bad request" + EscapeSequences.RESET_TEXT_COLOR;
                } else if (e.getMessage().equals("Error: unauthorized")) {
                    return EscapeSequences.RED + "This action is not authorized" + EscapeSequences.RESET_TEXT_COLOR;
                } else {
                    return EscapeSequences.RED + "Cannot create game due to internal server error" + EscapeSequences.RESET_TEXT_COLOR;
                }
            }
        }
        return EscapeSequences.RED + "Expected <gameID>. Check your number of parameters" + EscapeSequences.RESET_TEXT_COLOR;
    }

    public String listGames() throws ResponseException {
        try {
            assertSignedIn();
            GamesList games = (GamesList) server.listGames(authToken);
            int counter = 1;
            String prettyList = "";
            for(GameData game: games.games()){
                prettyList = prettyList.concat(counter +". "+ game.getGameName() + "\n");
                gameRefs.put(counter, game.getGameID());
                counter++;
            }
            return prettyList;

        }catch (ResponseException e){
            if(e.getMessage().contains("401")){
                return EscapeSequences.RED + "You aren't authorized to list games" + EscapeSequences.RESET_TEXT_COLOR;
            }
            else{
                return EscapeSequences.RED + "Cannot list due to internal server error" + EscapeSequences.RESET_TEXT_COLOR;
            }
        }
    }

    public String joinGame(String... params) throws ResponseException {
        if(params.length == 2) {
            try{
                Integer gameID = Integer.parseInt(params[0]);
            }catch(NumberFormatException n){
                return EscapeSequences.RED + "The game ID must be a number" + EscapeSequences.RESET_TEXT_COLOR;
            }
            if(     !params[1].equalsIgnoreCase("black") &&
                    !params[1].equalsIgnoreCase("white")){
                return EscapeSequences.RED + "Your second parameter 'player color' must be BLACK or WHITE" + EscapeSequences.RESET_TEXT_COLOR;
            }
            try {
                assertSignedIn();

                //SOMEHOW MY CLIENT NEEDS TO KEEP TRACK OF THE NUMBER OF THE GAMES FROM THE LAST TIME IT LISTED THE GAMES
                int gameID = gameRefs.get(Integer.parseInt(params[0]));
                server.joinGame(gameID, params[1], authToken);
                GameData theGame = null;

                GamesList games = (GamesList) server.listGames(authToken);
                for(GameData game: games.games()){

                    if (game.getGameID() == gameID){
                        theGame = game;
                    }
                }
                boolean isPlayer = true;
                if(theGame != null) {
                    if (params[1].equalsIgnoreCase("white")) {
                        ChessBoardUI.displayGame(theGame.getChessGame(), ChessGame.TeamColor.BLACK);
                        System.out.println(EscapeSequences.GREEN + "Successfully joined game " + params[0] + " as " + params[1] + EscapeSequences.RESET_TEXT_COLOR);
                        return new GamePlayUI(username, authToken, theGame, ChessGame.TeamColor.BLACK, isPlayer, gameID).run();
                    } else {
                        ChessBoardUI.displayGame(theGame.getChessGame(), ChessGame.TeamColor.WHITE);
                        System.out.println(EscapeSequences.GREEN + "Successfully joined game " + params[0] + " as " + params[1] + EscapeSequences.RESET_TEXT_COLOR);
                        return new GamePlayUI(username, authToken, theGame, ChessGame.TeamColor.WHITE, isPlayer, gameID).run();
                    }
                }else{
                    System.out.println("the game is null");
                }


            } catch (ResponseException e) {
                if (e.getMessage().contains("401")) {
                    return EscapeSequences.RED + "You must log in in order to perform this action" + EscapeSequences.RESET_TEXT_COLOR;
                } else if (e.getMessage().contains("400")) {
                    return EscapeSequences.RED + "This action is not authorized you must list first." + EscapeSequences.RESET_TEXT_COLOR;
                }
                else if(e.getMessage().contains("403")){
                    return EscapeSequences.RED + "That spot is already full" + EscapeSequences.RESET_TEXT_COLOR;

                }else {
                    return EscapeSequences.RED + "Cannot join game due to internal server error" + EscapeSequences.RESET_TEXT_COLOR;
                }
            } catch (IOException e) {
                System.out.println("sorry2");
            }
        }
        return EscapeSequences.RED + "Check your parameters: Expected <gameID> [WHITE][BLACK]" + EscapeSequences.RESET_TEXT_COLOR;
    }

    public String observeGame(String... params) throws ResponseException {
        Boolean isPlayer = false;
        if(params.length == 1) {
            try{
                Integer gameID = gameRefs.get(Integer.parseInt(params[0]));
                GameData theGame = null;

                GamesList games = (GamesList) server.listGames(authToken);
                for(GameData game: games.games()){

                    if (game.getGameID() == gameID){
                        theGame = game;
                    }
                }
                if(theGame != null) {
                    ChessBoardUI.displayGame(theGame.getChessGame(), ChessGame.TeamColor.BLACK);
                    System.out.println(EscapeSequences.GREEN + "Observing game " + params[0] + EscapeSequences.RESET_TEXT_COLOR);
                    return new GamePlayUI(username, authToken, theGame, ChessGame.TeamColor.WHITE, isPlayer, gameID).run();

                }

            }catch(NumberFormatException n){
                return EscapeSequences.RED + "The game ID must be a number" + EscapeSequences.RESET_TEXT_COLOR;
            } catch (IOException e) {
                System.out.println("sorry");
            }
        }
        return EscapeSequences.RED + "Check your parameters and try again: Expected <gameID>" + EscapeSequences.RESET_TEXT_COLOR;
    }

    public String logout() throws ResponseException {
        try {
            server.logout(authToken);
            System.out.println(EscapeSequences.GREEN + "Successfully logged out" + EscapeSequences.RESET_TEXT_COLOR);
            return new LoggedOut(serverUrl).run();

        }catch(ResponseException e){
            if(e.getMessage().contains("401")){
                return EscapeSequences.RED + "Logout unauthorized" + EscapeSequences.RESET_TEXT_COLOR;
            }
            else{
                return EscapeSequences.RED + "Cannot log out due to internal server error" + EscapeSequences.RESET_TEXT_COLOR;
            }
        }
    }


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

    private void assertSignedIn() throws ResponseException{
        if (state == State.LOGGEDOUT) {
            throw new ResponseException("User is not logged in");
        }
    }
}
