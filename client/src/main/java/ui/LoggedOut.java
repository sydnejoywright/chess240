package ui;

import com.google.gson.Gson;
import exception.ResponseException;

import java.util.Arrays;
import model.*;
import ui.ServerFacade.ServerFacade;

public class LoggedOut {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGEDOUT;

    public LoggedOut(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {//<USERNAME> <PASSWORD>
            try {
                server.login(params[0], params[1]);
                state = State.LOGGEDIN;
                return EscapeSequences.GREEN + "Successfully Logged In" + EscapeSequences.RESET_TEXT_COLOR;
            }catch(ResponseException e){

                if(e.getMessage().equals("Error: unauthorized")){
                    return EscapeSequences.RED + "Cannot log in due to invalid credentials" + EscapeSequences.RESET_TEXT_COLOR;
                }
                else{
                    return EscapeSequences.RED + "Cannot log in due to internal service error." + EscapeSequences.RESET_TEXT_COLOR;
                }
            }
        }
        return EscapeSequences.RED + "Expected <USERNAME> <PASSWORD>" + EscapeSequences.RESET_TEXT_COLOR;
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            try {
                server.register(params[0], params[1], params[2]);
                return EscapeSequences.GREEN + "Successfully Registered User" + EscapeSequences.RESET_TEXT_COLOR;
            }catch(ResponseException e){
                if(e.getMessage().equals("Error: already taken")){
                    return EscapeSequences.RED + "Sorry, that username is already taken." + EscapeSequences.RESET_TEXT_COLOR;
                }
                else if(e.getMessage().equals("Error: bad request")){
                    return EscapeSequences.RED + "Bad request" + EscapeSequences.RESET_TEXT_COLOR;
                }
                else{
                    return EscapeSequences.RED + "Cannot log in due to internal service error." + EscapeSequences.RESET_TEXT_COLOR;
                }
            }
        }
        return EscapeSequences.RED + "Expected <USERNAME> <PASSWORD> <EMAIL>" + EscapeSequences.RESET_TEXT_COLOR;    }


    public String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }
}
