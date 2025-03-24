//package ui;
//
//import java.util.Arrays;
//import ui.ServerFacade.ServerFacade;
//import com.google.gson.Gson;
//import model.*;
//import exception.ResponseException;
////import server.ServerFacade;
//
//public class ChessClient {
//    private String visitorName = null;
//    private final ServerFacade server;
//    private final String serverUrl;
////    private WebSocketFacade ws;
//    private State state = State.LOGGEDOUT;
//
//    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
//        server = new ServerFacade(serverUrl);
//        this.serverUrl = serverUrl;
//        this.notificationHandler = notificationHandler;
//    }
//
//    public String eval(String input) {
//        try {
//            var tokens = input.toLowerCase().split(" ");
//            var cmd = (tokens.length > 0) ? tokens[0] : "help";
//            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
//            return switch (cmd) {
//                case "quit" -> "quit";
//                case "login" -> login(params);
//                case "register" -> register(params);
//                case "logout" -> logout();
//                case "create game" -> createGame();
//                case "list games" -> listGames();
//                case "play game" -> playGame(params);
//                case "observe game" -> observeGame(params);
//                default -> help();
//            };
//        } catch (ResponseException ex) {
//            return ex.getMessage();
//        }
//    }
//
//    public String login(String... params) throws ResponseException {
//        if (params.length == 2) { //<USERNAME> <PASSWORD>
//
//            visitorName = String.join("-", params);
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
//            return String.format("You signed in as %s.", visitorName);
//            state = State.LOGGEDIN;
//        }
//        throw new ResponseException(400, "Expected: <yourname>");
//    }
//
//    public String register(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length >= 2) {
//            var name = params[0];
//            var type = PetType.valueOf(params[1].toUpperCase());
//            var pet = new Pet(0, name, type);
//            pet = server.addPet(pet);
//            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
//        }
//        throw new ResponseException(400, "Expected: <name> <CAT|DOG|FROG>");
//    }
//
//    public String logout() throws ResponseException {
//        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        ws = null;
//        state = State.LOGGEDOUT;
//        return String.format("%s left the shop", visitorName);
//
//    }
//
//    public String createGame() throws ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (var pet : server.listPets()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.deleteAllPets();
//        return buffer.toString();
//    }
//
//    public String listGames() throws ResponseException {
//        assertSignedIn();
//        var pets = server.listPets();
//        var result = new StringBuilder();
//        var gson = new Gson();
//        for (var pet : pets) {
//            result.append(gson.toJson(pet)).append('\n');
//        }
//        return result.toString();
//    }
//
//    public String playGame(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                var id = Integer.parseInt(params[0]);
//                var pet = getPet(id);
//                if (pet != null) {
//                    server.deletePet(id);
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new ResponseException(400, "Expected: <pet id>");
//    }
//
//    private Pet observeGame(int id) throws ResponseException {
//        for (var pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }
//
//    public String help() {
//        if (state == State.LOGGEDOUT) {
//            return """
//                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
//                    - login <USERNAME> <PASSWORD> - to play chess
//                    - quit - playing chess
//                    - help - with possible commands
//                    """;
//        }
//        return """
//                - create <NAME> - a game
//                - list - games
//                - join <ID> - a game
//                - logout - when you are done
//                - quit - playing chess
//                - help - with possible commands
//                """;
//    }
//
//    private void assertSignedIn() throws ResponseException {
//        if (state == State.LOGGEDOUT) {
//            throw new ResponseException(400, "You must sign in");
//        }
//    }
//}
