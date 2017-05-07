/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aifinalproject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import javax.swing.JButton;

/**
 *
 * @author alex
 */
public class GameBoard {
    private int firstMove; // which player went first
    private int currentPlayer; // keeps track of current player
    
    public final String player1Name; // self-explanatory
    private final String player2Name; // self-explanatory

    BoardState boardState; // current board state held in object
    private int gameWinner; // did someone win (-1 for no, 1 or 2 for yes)
    
    public static final int USER = 1; // define int value for user
    public static final int COMPUTER = 2; // definte int value for computer
    
    public static final int WIDTH = 4; // game board width
    public static final int HEIGHT = 4; // game board height
    
    private String gameMessage = ""; // message displayed at the top
    
    // globals for game statistics
    public long totalTime = 0; 
    public Boolean cutoffOccurred = false;
    public int depthReached = 0;
    public long nodesExplored = 0;
    public int maxValuePruning = 0;
    public int minValuePruning = 0;
    
    public int timeCutoff = 10000;
    public int depthCutoff = 0;
    public long startTime = 0;
    
    public int difficulty = 3;
    
    public GameBoard(String player1Name, String player2Name, int firstMove) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        
        resetGame(firstMove);
    }
    
    public void resetGame() {
        resetGame(0);
    }
    
    // reset the game board
    public final void resetGame(int firstMove) {
        this.firstMove = firstMove;
        this.currentPlayer = firstMove;
        this.boardState = new BoardState(WIDTH, HEIGHT);
        this.gameWinner = -1;
        
        updateMessage();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    public String getCurrentPlayerName() {
        return getPlayerName(currentPlayer);
    }
    
    public String getPlayerName(int player) {
        if (player == USER) return player1Name;
        else return player2Name;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }
    
    public String getGameMessage() {
        return gameMessage;
    }

    public int[] getMoves() {
        return boardState.moves;
    }

    public int getGameWinner() {
        return gameWinner;
    }
    
    private int otherPlayer(int player) {
        if (player == USER) return COMPUTER;
        else return USER;
    }
    
    public void switchPlayers() {
        currentPlayer = otherPlayer(currentPlayer);
    }
    
    // get X or O for the current player
    public String getMoveForPlayer(int player) {
        if (player == USER) return "O";
        if (player == COMPUTER) return "X";
        return "";
    }
    
    public void playerMove(int position) {
        if (boardState.makeMove(USER, position)) {
            switchPlayers();
        }
        
        updateMessage();
    }
    
    // algorith for computer move using A-B pruning
    public void computerMove() {  
        if (gameWinner != -1) return;
        
        totalTime = 0;
        cutoffOccurred = false;
        nodesExplored = 1;
        depthReached = 0;
        maxValuePruning = 0;
        minValuePruning = 0;
        
        startTime = System.currentTimeMillis();
        
        int move = 0;
        
        switch (difficulty) {
            case 3:
                move = getBestMove(boardState);
                break;
            case 2:
                Random random = new Random();
                int chance = random.nextInt(100);
                if (chance > 70) move = getBestMove(boardState);
                else move = getRandomMove(boardState);
                break;
            case 1: 
                move = getRandomMove(boardState);
                break;
            default:
                move = getBestMove(boardState);
                break;
        }
        
        boardState.makeMove(COMPUTER, move);
        
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Computer move took " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds to explore " + addCommas(nodesExplored) + " nodes.");
        
        switchPlayers();
        
        updateMessage();
    }
    
    private int getBestMove(BoardState state) {
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(currentPlayer);
        
        ArrayList<Integer> moveChoices = new ArrayList<>();
        
        int bestMoveValue = Integer.MIN_VALUE;
        
        for (int i = 0; i < possibleMoves.size(); i++) {
            int moveValue = minValue(possibleMoves.get(i), -1000, 1000, 0);

            if (moveValue > bestMoveValue) {
                moveChoices = new ArrayList();
                moveChoices.add(i);
                bestMoveValue = moveValue;
            } else if (moveValue == bestMoveValue) {
                moveChoices.add(i);
            }
        }
        
        Random random = new Random();
        return possibleMoves.get(moveChoices.get(random.nextInt(moveChoices.size()))).lastMove.y;
    }
    
    private int getRandomMove(BoardState state) {
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(currentPlayer);

        Random random = new Random();
        return possibleMoves.get(random.nextInt(possibleMoves.size())).lastMove.y;
    }
    
    // function to add 1000s commas to integers
    public String addCommas(long input) {
      NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
      String numberAsString = numberFormat.format(input);
      return numberAsString;
    }
    
    private Boolean terminalTest(BoardState state) {
        return state.checkTie() || state.checkWin(USER) || state.checkWin(COMPUTER);
    }
    
    private int utilityValue(BoardState state, int depth) {
        if (state.checkWin(COMPUTER)) {
            return 1000 - depth; // adjust the value with the depth
        }
        
        if (state.checkWin(USER)) {
//            if (difficulty == 2) return 0;
            
            return depth - 1000; // adjust the value with the depth
        } 
        
        if (state.checkTie()) {
            return 0;
        }
        
        return 0;
    }

    private int maxValue(BoardState state, int a, int b, int currentDepth) {
        nodesExplored++;
        if (currentDepth > depthReached) depthReached = currentDepth;
        
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(COMPUTER);
        
        if (terminalTest(state)) return utilityValue(state, currentDepth);
        
        if (depthCutoff > 0 && currentDepth >= depthCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state);
        }
        
        if ((System.currentTimeMillis() - startTime) > timeCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state);
        }
        
        int v = Integer.MIN_VALUE;
        
        for (int i = 0; i < possibleMoves.size(); i++) {
            v = Math.max(v, minValue(possibleMoves.get(i), a, b, currentDepth++));
            if (v >= b) {
                maxValuePruning++;
                return v;
            }
            a = Math.max(a, v);
        }
        
        return v;
    }
    
    private int minValue(BoardState state, int a, int b, int currentDepth) {
        nodesExplored++;
        if (currentDepth > depthReached) depthReached = currentDepth;
        
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(USER);
        
        if (terminalTest(state)) return utilityValue(state, currentDepth);
        
        if (depthCutoff > 0 && currentDepth >= depthCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state);
        }
        
        if ((System.currentTimeMillis() - startTime) > timeCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state);
        }
        
        int v = Integer.MAX_VALUE;
        
        for (int i = 0; i < possibleMoves.size(); i++) {
            v = Math.min(v, maxValue(possibleMoves.get(i), a, b, currentDepth++));
            if (v <= a) {
                minValuePruning++;
                return v;
            }
            b = Math.min(b, v);
        }
        
        return v;
    }
    
    // evaluation function
    private int evaluateBoard(BoardState boardState) {
        if (difficulty == 3) {
            int X3 = boardState.checkNumPlays(firstMove, 3);
            int X2 = boardState.checkNumPlays(firstMove, 2);
            int X1 = boardState.checkNumPlays(firstMove, 1);
            int O3 = boardState.checkNumPlays(otherPlayer(firstMove), 3);
            int O2 = boardState.checkNumPlays(otherPlayer(firstMove), 2);
            int O1 = boardState.checkNumPlays(otherPlayer(firstMove), 1);

            return 6 * X3 + 3 * X2 + X1 - (6 * O3 + 3 * O2 + O1);
        } else {
            int X3 = boardState.checkNumPlays(firstMove, 3);
            int X2 = boardState.checkNumPlays(firstMove, 2);
            int X1 = boardState.checkNumPlays(firstMove, 1);
            int O3 = boardState.checkNumPlays(otherPlayer(firstMove), 3);
            int O2 = boardState.checkNumPlays(otherPlayer(firstMove), 2);
            int O1 = boardState.checkNumPlays(otherPlayer(firstMove), 1);

            return 6 * X3 + 3 * X2 + X1 - (6 * O3 + 3 * O2 + O1);
        }
    }
    
    // button pressed handler
    // if no winner, let the player move
    // after player moves, call again to trigger computer move
    public void buttonPressed(int c, int r) {
        System.out.println("button pressed (" + c + ", " + r + ")");
        
        if (gameWinner == -1) {
            if (currentPlayer == USER) {
                playerMove(c + r * WIDTH);
            } else {
                computerMove();
            }
        }
        
        updateMessage();
    }
    
    // update the global message based on the game stats
    private void updateMessage() {
        System.out.println("update");
        if (boardState.checkWin(USER)) {
            gameWinner = USER;
            System.out.println("USER wins");
        } else if (boardState.checkWin(COMPUTER)) {
            gameWinner = COMPUTER;
            System.out.println("COMPUTER wins");
        } else if (boardState.checkTie()) {
            gameWinner = 0;
            System.out.println("It's a tie");
        }
        
        switch (gameWinner) {
            case USER:
            case COMPUTER:
                gameMessage = getPlayerName(gameWinner) + " has won!";
                break;
            case 0:
                gameMessage = "It's a tie";
                break;
            default:
                gameMessage = "It is " + getPlayerName(currentPlayer) + "'s turn";
                break;
        }
    }
}
