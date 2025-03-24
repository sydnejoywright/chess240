package ui;

import com.google.gson.Gson;
import exception.ResponseException;

import java.util.Arrays;
import java.util.Scanner;

import model.*;
import ui.ServerFacade.ServerFacade;

import static ui.EscapeSequences.GREEN;

public class LoggedOut {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGEDOUT;

    public LoggedOut(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String run() {
        System.out.println("\uD83D\uDC36 Welcome to 240 chess. Type Help to get started.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(EscapeSequences.BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + "[LOGGED OUT] >>> " + GREEN);
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
                AuthtokenData authtokenData = server.login(params[0], params[1]);
                state = State.LOGGEDIN;
                System.out.println(EscapeSequences.GREEN + "Logged in as " + authtokenData.username + EscapeSequences.RESET_TEXT_COLOR);
                String authToken = authtokenData.authToken;
                return new LoggedIn(serverUrl, authToken).run();
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
                RegisterResult registerResult = server.register(params[0], params[1], params[2]);
                System.out.println(EscapeSequences.GREEN + "Successfully Registered User" + EscapeSequences.RESET_TEXT_COLOR);
                return new LoggedIn(serverUrl, registerResult.authToken()).run();
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
