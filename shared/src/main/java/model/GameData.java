package model;

import chess.ChessGame;

public class GameData {
    private int GameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;


    public void setGameName(String gameName){
        this.gameName = gameName;
    }

    public String getGameName(){
        return gameName;
    }

    public void setGameID(int number){
        this.GameID = number;
    }

    public int getGameID(){
        return GameID;
    }

    public void setWhiteUsername(String name){
        this.whiteUsername = name;
    }

    public String getWhiteUsername(){
        return whiteUsername;
    }

    public void setBlackUsername(String name){
        this.blackUsername = name;
    }

    public String getBlackUsername(){
        return blackUsername;
    }


    public ChessGame getChessGame(){
        return game;
    }




}
