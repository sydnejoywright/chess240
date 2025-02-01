package chess;

import java.awt.dnd.InvalidDnDOperationException;
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
        Collection<ChessMove> invalidMoves = new ArrayList<>();
        Collection<ChessMove> validMovesAfterChecks = new ArrayList<>();
        //if no piece at startPosition, return null
        ChessPiece piece = gameBoard.getPiece(startPosition);
        if(piece == null ){
            return null;
        }
        else{
            //first, just let all the moves be valid and we will slowly delete from this collection
            validMovesAfterChecks = piece.pieceMoves(gameBoard, startPosition);
        }
        //will making this move cause my king to be in check?
        for(ChessMove move : validMovesAfterChecks) {
            ChessPosition end = move.getEndPosition();
            ChessPiece captured = gameBoard.getPiece(end);

            gameBoard.addPiece(end, piece);
            gameBoard.addPiece(startPosition, null);
            if(isInCheck(piece.getTeamColor())){
                invalidMoves.add(move); //can't do the move
            }
            //even if it's not in check i still need to reset the board.
            gameBoard.addPiece(startPosition, piece);
            gameBoard.addPiece(end, captured);
        }
        validMovesAfterChecks.removeAll(invalidMoves);
        return validMovesAfterChecks;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();          //separating out the class variables from Move so they can be more easily used.
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = gameBoard.getPiece(start);
        ChessPiece.PieceType promotionPieceType = move.getPromotionPiece();
        ChessPiece promoPiece = new ChessPiece(teamTurn, promotionPieceType);

        if(piece == null){
            throw new InvalidMoveException("There's no piece there to move");
        }

        if(!(validMoves(start).contains(move))){
            throw new InvalidMoveException("Sorry! That's not a valid move.");
        }
        if(!(teamTurn == piece.getTeamColor())){
            throw new InvalidMoveException("It's not your turn my brotha");
        }
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (end.getRow() == 1 || end.getRow() == 8) {             //if we need to promote:
                gameBoard.addPiece(end, promoPiece);
                gameBoard.addPiece(start, null);
            }
            else{
                gameBoard.addPiece(end, piece);
                gameBoard.addPiece(start, null);
            }
        }
        else {              //if we aren't promoting the promotion piece is null
            gameBoard.addPiece(end, piece);
            gameBoard.addPiece(start, null);
        }
        changeTurns(piece.getTeamColor());
    }

    public void changeTurns(TeamColor team){
        if(team == TeamColor.BLACK){         //after the move is done, it is the other team's turn
            teamTurn = TeamColor.WHITE;
        }
        else{
            teamTurn = TeamColor.BLACK;
        }
    }

    public TeamColor otherTeam(TeamColor team){
        if(team == TeamColor.BLACK){         //after the move is done, it is the other team's turn
            return TeamColor.WHITE;
        }
        else{
            return TeamColor.BLACK;
        }
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
        for(int i=1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard,position);
                    if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
                        ChessMove checkKingpromoteBishop = new ChessMove(position,kingsPosition,ChessPiece.PieceType.BISHOP);
                        ChessMove checkKingpromoteRook = new ChessMove(position,kingsPosition,ChessPiece.PieceType.ROOK);
                        ChessMove checkKingpromoteKnight = new ChessMove(position,kingsPosition,ChessPiece.PieceType.KNIGHT);
                        ChessMove checkKingpromoteQueen = new ChessMove(position,kingsPosition, ChessPiece.PieceType.QUEEN);
                        ChessMove checkKingpromoteNull = new ChessMove(position,kingsPosition, null);
                        if (
                            moves.contains(checkKingpromoteBishop)||
                            moves.contains(checkKingpromoteRook)||
                            moves.contains(checkKingpromoteKnight)||
                            moves.contains(checkKingpromoteQueen)||
                            moves.contains(checkKingpromoteNull)){
                            return true;
                        }
                    }
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

    public ChessPosition findThreat(TeamColor teamColor){
        ChessPosition kingsPosition = findKing(teamColor);
        for(int i=1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard,position);
                    //create a move that would capture the king from the current position of the piece
                    if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
                        ChessMove checkKingpromoteBishop = new ChessMove(position,kingsPosition,ChessPiece.PieceType.BISHOP);
                        ChessMove checkKingpromoteRook = new ChessMove(position,kingsPosition,ChessPiece.PieceType.ROOK);
                        ChessMove checkKingpromoteKnight = new ChessMove(position,kingsPosition,ChessPiece.PieceType.KNIGHT);
                        ChessMove checkKingpromoteQueen = new ChessMove(position,kingsPosition, ChessPiece.PieceType.QUEEN);
                        ChessMove checkKingpromoteNull = new ChessMove(position,kingsPosition, null);
                        if (
                                moves.contains(checkKingpromoteBishop)||
                                        moves.contains(checkKingpromoteRook)||
                                        moves.contains(checkKingpromoteKnight)||
                                        moves.contains(checkKingpromoteQueen)||
                                        moves.contains(checkKingpromoteNull)) {
                            return position;
                        }
                    }
                    ChessMove checkKing = new ChessMove(position,kingsPosition,null);
                    if (moves.contains(checkKing)) {
                        return position;
                    }
                }
            }
        }
        return null;
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
            //if the king himself has a valid move then he can escape check and we are not in checkmate.
        if (!validMoves(findKing(teamColor)).isEmpty()) {
            return false;
        }

        ChessPosition threateningPiece = findThreat(teamColor);
        ChessPiece.PieceType threatType = gameBoard.getPiece(threateningPiece).getPieceType();
            //can we capture the piece that is putting us in check?
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard, position);
                    //create a move that would capture the king from the current position of the piece
                    ChessMove killThreat = new ChessMove(position, threateningPiece, null);
                    if (moves.contains(killThreat)) {
                        if(isInCheck(teamColor)){        //capturing the peice results in check
                            return true;
                        }
                        else{
                            return false;
                        }
                    }

            }
        }
        // can we block the piece that is putting us in check?
        if (threatType == ChessPiece.PieceType.KNIGHT) {      //knights can't be blocked so this must be checkmate
            return true;
        }
        else {            //loop through all of the possible moves for my team, try to make the move and see if it gets you out of check. if it does, you are not in checkmate.
            for (int l = 1; l <= 8; l++) {
                for (int n = 1; n <= 8; n++) {
                    ChessPosition position = new ChessPosition(l, n);
                    ChessPiece piece = gameBoard.getPiece(position);

                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = piece.pieceMoves(gameBoard, position);
                        for (ChessMove move : moves) {
                            ChessPosition end = move.getEndPosition();
                            ChessPiece captured = gameBoard.getPiece(end);

                            gameBoard.addPiece(end, piece);
                            gameBoard.addPiece(position, null);
                            if (!isInCheck(piece.getTeamColor())) {
                                return false; //can't do the move
                            }
                            //even if it's not in check i still need to reset the board.
                            gameBoard.addPiece(position, piece);
                            gameBoard.addPiece(end, captured);
                        }
                    }
                }
            }
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
        //if the king is not in check a stalemate means that you can't make any valid moves
        for(int i=1; i <= 8; i++) {            //loop through all the pieces of my color, if any of them have valid moves return false
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor){
                    if(!validMoves(position).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
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
                "}";
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
        for(int i=1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = game.getPiece(position);
                if(piece != null){
                    if ((piece.getPieceType() == ChessPiece.PieceType.KING) &&     //checks the board at the current positions. if it is a king
                            game.getPiece(position).getTeamColor() == team) {                        //and checks if it is the king for the team that we are looking for.
                        return position;
                    }
                }
            }
        }
        return null;
    }
}
