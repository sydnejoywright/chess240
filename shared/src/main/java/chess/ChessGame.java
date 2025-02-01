package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard gameBoard;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        this.gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMovesAfterChecks = new ArrayList<>();
        //if no piece at startPosition, return null
        ChessPiece piece = gameBoard.getPiece(startPosition);
        if(piece == null ){
            return null;
        }
        else{
            //these are the valid moves at the start position before we verify the rules
            Collection<ChessMove> validMovesBeforeChecks = piece.pieceMoves(gameBoard,startPosition);
        }
        return validMovesAfterChecks;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find out where the king is on the board and save his position
        ChessPosition kingsPosition = findKing(teamColor);
        //run valid moves for all the pieces and see if the kings position is in the list for any of them. if so, he is in check.
        for(int i=1; i < 8; i++) {
            for (int j = 1; j < 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard,position);
                    //create a move that would capture the king from the current position of the piece
                    ChessMove checkKing = new ChessMove(position,kingsPosition,null);
                    if (moves.contains(checkKing)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){  //if the king is not in check then it cannot be checkmate
            return false;
        }
        else {
            if ((validMoves(findKing(teamColor)).isEmpty())) {//if king has no valid moves it's array of possible moves will be empty
                //go through all the other pieces that are this team's and see if they can block
//                if(){                                                               //if other pieces have no valid moves that can block check.
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){ //cannot be a stalemate if the king is not in check
            return false;
        }
        ChessPosition kingPosition = findKing(teamColor);
        if(validMoves(kingPosition).isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", gameBoard=" + gameBoard +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }

    public ChessPosition findKing(TeamColor team){
        //team tells me which color king I am looking for
        ChessBoard game = getBoard();
        for(int i=1; i < 8; i++) {
            for (int j = 1; j < 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                if(game.getPiece(position) != null){
                    if ((game.getPiece(position).getPieceType() == ChessPiece.PieceType.KING) &&     //checks the board at the current positions. if it is a king
                            game.getPiece(position).getTeamColor() == team) {                        //and checks if it is the king for the team that we are looking for.
                        return position;
                    }
                }
            }
        }
        return null;
    }
}
