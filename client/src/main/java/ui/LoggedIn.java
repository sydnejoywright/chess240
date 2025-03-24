package ui;

import java.util.Arrays;

import chess.ChessGame;
import ui.ServerFacade.ServerFacade;
import com.google.gson.Gson;
import model.*;
import exception.ResponseException;
//import server.ServerFacade;

public class LoggedIn {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
//    private WebSocketFacade ws;
    private State state = State.LOGGEDIN;

    public LoggedIn(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> playGame(params);
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
        if(params.length == 2) {
            try {
                assertSignedIn();
                server.createGame(params[0]);
                System.out.println(EscapeSequences.GREEN + "Successfully created game" + EscapeSequences.RESET_TEXT_COLOR);
                return listGames();

            } catch (ResponseException e) {
                if (e.getMessage().equals("User is not logged in")) {
                    return EscapeSequences.RED + "You must log in in order to perform this action" + EscapeSequences.RESET_TEXT_COLOR;
                } else if (e.getMessage().equals("Error: bad request")) {
                    return EscapeSequences.RED + "Bad request" + EscapeSequences.RESET_TEXT_COLOR;
                } else if (e.getMessage().equals("Error: unauthorized")) {
                    return EscapeSequences.RED + "This action is not authorized" + EscapeSequences.RESET_TEXT_COLOR;
                } else {
                    return EscapeSequences.RED + "Cannot log out due to internal server error" + EscapeSequences.RESET_TEXT_COLOR;
                }
            }
        }
        return EscapeSequences.RED + "Expected <gameID> [WHITE][BLACK]" + EscapeSequences.RESET_TEXT_COLOR;
    }

    public String listGames() throws ResponseException {
        try {
            assertSignedIn();
            return server.listGames();
        }catch (ResponseException e){
            if(e.getMessage().equals("User is not logged in")){
                return EscapeSequences.RED + "You must log in in order to perform this action" + EscapeSequences.RESET_TEXT_COLOR;
            }
            else if(e.getMessage().equals("Error: unauthorized")){
                return EscapeSequences.RED + "This action is not authorized" + EscapeSequences.RESET_TEXT_COLOR;
            }
            else{
                return EscapeSequences.RED + "Cannot log out due to internal server error" + EscapeSequences.RESET_TEXT_COLOR;
            }
        }
    }


    public String playGame(String teamColor, Integer gameID) throws ResponseException {
        try {
            assertSignedIn();
            //SOMEHOW MY CLIENT NEEDS TO KEEP TRACK OF THE NUMBER OF THE GAMES FROM THE LAST TIME IT LISTED THE GAMES
            server.joinGame(gameID, teamColor);
            return EscapeSequences.GREEN + "Successfully joined game" + EscapeSequences.RESET_TEXT_COLOR;

        }catch (ResponseException e){
            if(e.getMessage().equals("User is not logged in")){
                return EscapeSequences.RED + "You must log in in order to perform this action" + EscapeSequences.RESET_TEXT_COLOR;
            }
            else if(e.getMessage().equals("Error: unauthorized")){
                return EscapeSequences.RED + "This action is not authorized" + EscapeSequences.RESET_TEXT_COLOR;
            }
            else{
                return EscapeSequences.RED + "Cannot log out due to internal server error" + EscapeSequences.RESET_TEXT_COLOR;
            }
        }
    }

    public String observeGame() throws ResponseException {
        assertSignedIn();
        var pets = server.listPets();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var pet : pets) {
            result.append(gson.toJson(pet)).append('\n');
        }
        return result.toString();
    }

    public String logout() throws ResponseException {
        try {
            assertSignedIn();
            server.logout();
            return EscapeSequences.GREEN + "Successfully logged out" + EscapeSequences.RESET_TEXT_COLOR;

        }catch(ResponseException e){
            if(e.getMessage().equals("Error: unauthorized")){
                return EscapeSequences.RED + "Logout unauthorized" + EscapeSequences.RESET_TEXT_COLOR;
            }
            else if(e.getMessage().equals("User is not logged in")){
                return EscapeSequences.RED + "You must log in in order to perform this action" + EscapeSequences.RESET_TEXT_COLOR;
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
