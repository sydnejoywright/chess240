package ui;
import chess.ChessGame;
import chess.ChessPiece;
import static ui.EscapeSequences.*;


import javax.swing.text.TabExpander;

public class ChessBoardUI {
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE;
    private static final String WHITE_TILE = SET_BG_COLOR_WHITE;
    private static final String BLACK_TILE = SET_BG_COLOR_BLACK;
    private static final String WHITE_TEAM = RED;
    private static final String BLACK_TEAM = BLUE;

    public void displayGame(ChessGame game, ChessGame.TeamColor asTeam){

    }

    public void horizontalBorder(){}

    public String makeSquare(String color, String character){
        return color+" "+character+" ";
    }

    public void makeRow(int row){
        String rowSquares = "";
        rowSquares+= makeSquare(BORDER_COLOR, Integer.toString(row));
        for(int i=1; i<=8; i++) {
            String squareColor;
            if ((row + i) % 2 == 0) {
                squareColor = BLACK_TILE;
            }
            else{
                squareColor = WHITE_TILE;
            }
            rowSquares += makeSquare(squareColor, " ");
        }
        rowSquares+= makeSquare(BORDER_COLOR, Integer.toString(row));
        System.out.print(rowSquares);
    }

    public void body(){
        for(int i = 0; i < 8; i++){
            makeRow();
        }
    }
}
