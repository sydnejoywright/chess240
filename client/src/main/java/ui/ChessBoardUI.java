package ui;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;


import javax.swing.text.TabExpander;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessBoardUI {
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE;
    private static final String WHITE_TILE = SET_BG_COLOR_WHITE;
    private static final String BLACK_TILE = SET_BG_COLOR_BLACK;
    private static final String WHITE_TEAM = RED;
    private static final String BLACK_TEAM = BLUE;
    private static final List<String> letters = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");

    public static void displayGame(ChessGame game, ChessGame.TeamColor asTeam) {
        horizontalBorder(asTeam);
        if(asTeam == ChessGame.TeamColor.WHITE) {
            for (int i = 1; i <= 8; i++) {
                makeRow(i, game);
            }
        }
        else{
            for (int i = 8; i >= 1; i--) {
                makeRow(i, game);

            }
        }
        horizontalBorder(asTeam);
    }

    public static void horizontalBorder(ChessGame.TeamColor team) {
        String borderSquares = "";
        borderSquares += makeSquare(BORDER_COLOR, " ");
        if(team == ChessGame.TeamColor.WHITE){
            for(int i=7; i>=0; i--){
                borderSquares+= makeSquare(BORDER_COLOR, letters.get(i));
            }
        }else{
            for(int i=0; i<8; i++){
                borderSquares+= makeSquare(BORDER_COLOR, letters.get(i));
            }
        }
        borderSquares += makeSquare(BORDER_COLOR, " ");
        System.out.println(borderSquares + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    public static String makeSquare(String color, String character) {
        return color + " " + character + " ";
    }

    public static void makeRow(int row, ChessGame game) {
        String rowSquares = "";
        rowSquares += makeSquare(BORDER_COLOR, Integer.toString(row));
        for (int i = 1; i <= 8; i++) {
            String squareColor;
            if ((row + i) % 2 == 0) {
            squareColor = BLACK_TILE;
            } else {
                squareColor = WHITE_TILE;
            }
            rowSquares += makeSquare(squareColor, findPiece(row, i, game));
        }
        rowSquares += makeSquare(BORDER_COLOR, Integer.toString(row));
        System.out.println(rowSquares + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    public static String findPiece(int row, int col, ChessGame game){
        ChessBoard board = game.getBoard();
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if(piece == null){
            return " ";
        }
        String teamColor = (piece.getTeamColor() == ChessGame.TeamColor.BLACK)?BLACK_TEAM:WHITE_TEAM;
        return switch (piece.getPieceType()){
            case KING -> teamColor + "K";
            case QUEEN -> teamColor + "Q";
            case BISHOP -> teamColor + "B";
            case KNIGHT -> teamColor + "N";
            case ROOK -> teamColor + "R";
            case PAWN -> teamColor + "P";
            default -> " ";
        };
    }
}
