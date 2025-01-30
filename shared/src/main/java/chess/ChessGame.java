package chess;

import java.util.ArrayList;
import java.util.Collection;


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
        if(gameBoard.getPiece(startPosition) == null ){
            return null;
        }
        else{
            //these are the valid moves at the start position before we verify the rules
            Collection<ChessMove> validMovesBeforeChecks = gameBoard.getPiece(startPosition).pieceMoves(gameBoard,startPosition);
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
//        //run valid moves for all the pieces and see if the kings position is in the list for any of them. if so, he is in check.
//        for(int i=0; i<8; i ++) {
//            for (int j = 0; j < 8; j++) {
//                ChessPosition position = new ChessPosition(i, j);
//                ChessPiece piece = gameBoard.getPiece(position);
//
////                if(piece != null && piece.getTeamColor() != teamColor){
//                    Collection<ChessMove> moves = validMoves(position);
//                        }
//                    }
////                }
//            }
//        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //if is in check
        if(isInCheck(teamColor)){
            if((validMoves(findKing(teamColor)).isEmpty())){//if king has no valid moves it's array of possible moves will be empty
                //go through all the other pieces that are this team's and see if they can block
//                if(){                                                               //if other pieces have no valid moves that can block check.

            }


            //then the king is in checkmate
                return true;
        }
        else{ //if the king is not in check it cannot be checkmate
            return false;
        }
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
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    public ChessPosition findKing(TeamColor team){
        //team tells me which color king I am looking for
        for(int i=0; i<8; i ++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                if ((gameBoard.getPiece(position).getPieceType() == ChessPiece.PieceType.KING) &&     //checks the board at the current positions. if it is a king
                        gameBoard.getPiece(position).getTeamColor() == team) {                        //and checks if it is the king for the team that we are looking for.
                    return position;
                }
            }
        }
        return null;
    }
}
