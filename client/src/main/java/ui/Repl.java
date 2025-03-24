//package ui;
//
//import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;
//
//import java.util.Scanner;
//
//import static client.EscapeSequences.*;
//
//public class Repl implements NotificationHandler {
//    private final ChessClient client;
//
//    public Repl(String serverUrl) {
//        client = new ChessClient(serverUrl, this);
//    }
//
//    public void run() {
//        System.out.println("\uD83D\uDC36 Welcome to 240 chess. Type Help to get started.");
//        System.out.print(client.help());
//
//        Scanner scanner = new Scanner(System.in);
//        var result = "";
//        while (!result.equals("quit")) {
//            printPrompt();
//            String line = scanner.nextLine();
//
//            try {
//                result = client.eval(line);
//                System.out.print(EscapeSequences.BLUE + result);
//            } catch (Throwable e) {
//                var msg = e.toString();
//                System.out.print(msg);
//            }
//        }
//        System.out.println();
//    }
//
//    public void notify(Notification notification) {
//        System.out.println(EscapeSequences.RED + notification.message());
//        printPrompt();
//    }
//
//    private void printPrompt() {
//        System.out.print("\n" + EscapeSequences.RESET + ">>> " + GREEN);
//    }