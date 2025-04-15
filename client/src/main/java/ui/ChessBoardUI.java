package ui;
import chess.*;

import static ui.EscapeSequences.*;


import javax.swing.text.TabExpander;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ChessBoardUI {
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE;
    private static final String WHITE_TILE = SET_BG_COLOR_WHITE;
    private static final String BLACK_TILE = SET_BG_COLOR_BLACK;
    private static final String WHITE_TEAM = RED;
    private static final String BLACK_TEAM = BLUE;
    private static final String HIGHLIGHT_BLACK = SET_BG_COLOR_DARK_GREEN;
    private static final String HIGHLIGHT_WHITE = SET_BG_COLOR_GREEN;
    private static final List<String> LETTERS = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");

    public static void highlightGame(ChessGame game, ChessGame.TeamColor asTeam, ChessPosition highlightThis) {
        asTeam = (asTeam == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        Collection<ChessMove> validMoves = game.validMoves(highlightThis);
        Collection<ChessPosition> highlightThese = new ArrayList<>(List.of());
        for (ChessMove move : validMoves) {
            highlightThese.add(move.getEndPosition());
        }
        horizontalBorder(asTeam);
        if(asTeam == ChessGame.TeamColor.WHITE) {
            for (int i = 1; i <= 8; i++) {
                makeRow(i, game, asTeam, highlightThese, highlightThis);
            }
        }
        else{
            for (int i = 8; i >= 1; i--) {
                makeRow(i, game, asTeam, highlightThese, highlightThis);
            }
        }
        horizontalBorder(asTeam);
    }

    public static void displayGame(ChessGame game, ChessGame.TeamColor asTeam) {
        asTeam = (asTeam == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        horizontalBorder(asTeam);
        if(asTeam == ChessGame.TeamColor.WHITE) {
            for (int i = 1; i <= 8; i++) {
                makeRow(i, game, asTeam, null, null);
            }
        }
        else{
            for (int i = 8; i >= 1; i--) {
                makeRow(i, game, asTeam, null, null);
            }
        }
        horizontalBorder(asTeam);
    }

    public static void horizontalBorder(ChessGame.TeamColor team) {
        String borderSquares = "";
        borderSquares += makeSquare(BORDER_COLOR, " ");
        if(team == ChessGame.TeamColor.WHITE){
            for(int i=7; i>=0; i--){
                borderSquares+= makeSquare(BORDER_COLOR, LETTERS.get(i));
            }
        }else{
            for(int i=0; i<8; i++){
                borderSquares+= makeSquare(BORDER_COLOR, LETTERS.get(i));
            }
        }
        borderSquares += makeSquare(BORDER_COLOR, " ");
        System.out.println(borderSquares + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    public static String makeSquare(String color, String character) {
        return color + " " + character + " ";
    }

    public static void makeRow(int row, ChessGame game, ChessGame.TeamColor asTeam, Collection<ChessPosition> highlightThese, ChessPosition highlightThis) {
        String rowSquares = "";
        rowSquares += makeSquare(BORDER_COLOR, Integer.toString(row));
        if(!asTeam.equals(ChessGame.TeamColor.WHITE)) {
            for (int i = 1; i <= 8; i++) {
                String squareColor;
                if (highlightThis != null  && highlightThis.equals(new ChessPosition(row, i))) {
                    squareColor = SET_BG_COLOR_YELLOW;
                } else if ((row + i) % 2 == 0) {
                    squareColor = (highlightThese != null && highlightThese.contains(new ChessPosition(row, i))) ? HIGHLIGHT_BLACK : BLACK_TILE;
                } else {
                    squareColor = (highlightThese != null && highlightThese.contains(new ChessPosition(row, i))) ? HIGHLIGHT_WHITE : WHITE_TILE;
                }
                rowSquares += makeSquare(squareColor, findPiece(row, i, game));
            }
        }else{
            for (int i = 8; i >= 1; i--) {
                String squareColor;
                if (highlightThis != null  && highlightThis.equals(new ChessPosition(row, i))) {
                    squareColor = SET_BG_COLOR_YELLOW;
                } else if ((row + i) % 2 == 0) {
                    squareColor = (highlightThese != null && highlightThese.contains(new ChessPosition(row, i))) ? HIGHLIGHT_BLACK : BLACK_TILE;
                } else {
                    squareColor = (highlightThese != null && highlightThese.contains(new ChessPosition(row, i))) ? HIGHLIGHT_WHITE : WHITE_TILE;
                }
                rowSquares += makeSquare(squareColor, findPiece(row, i, game));
            }
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
