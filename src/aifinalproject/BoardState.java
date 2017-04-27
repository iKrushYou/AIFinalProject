/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aifinalproject;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author alex
 */
public class BoardState {
    int[] moves;
    
    private final int WIDTH; // game width
    private final int HEIGHT; // game height
    
    Point lastMove; // "coordinates" of last move (player, moveLocation)
    
    public BoardState(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        
        this.moves = new int[width*height];
        
        this.lastMove = new Point(-1, -1);
    }
    
    // copy board state into new one
    public BoardState(BoardState boardState) {
        this.moves = new int[boardState.moves.length];
        System.arraycopy(boardState.moves, 0, this.moves, 0, boardState.moves.length);
        this.WIDTH = boardState.WIDTH;
        this.HEIGHT = boardState.HEIGHT;
        this.lastMove = new Point(boardState.lastMove);
    }
    
    // translate column / row to a position (0 - 15)
    private int getPositionForMove(int c, int r) {
        return c + r * WIDTH;
    }
    
    // set move in moves array for player
    // if player 1 went in row 3, col 1:
    // moves[8] = 1;
    private void setMove(int player, int position) {
        moves[position] = player;
        lastMove = new Point(player, position);
    }
    
    // check if move is availible
    private Boolean checkMove(int position) {
        return moves[position] == 0;
    }
    
    // set move if move is availible
    public Boolean makeMove(int player, int position) {
        if (!checkMove(position)) return false;
              
        setMove(player, position);
        
        return true;
    }
    
    // check for win cases
    public Boolean checkWin(int player) {      
        int count;
        
        // check horizontals
        for (int i = 0; i < HEIGHT; i++) {
            count = 0;
            for (int j = 0; j < WIDTH; j++) {
                if (moves[getPositionForMove(j, i)] == player) count++;
            }
            
            if (count == WIDTH) return true;
        }
        
        // check verticals
        for (int i = 0; i < WIDTH; i++) {
            count = 0;
            for (int j = 0; j < HEIGHT; j++) {
                if (moves[getPositionForMove(i, j)] == player) count++;
            }
            
            if (count == WIDTH) return true;
        }
        
        // check horizontals
        count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            if (moves[getPositionForMove(i, i)] == player) count++;
        }
        if (count == HEIGHT) return true;
        
        count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            if (moves[getPositionForMove(i, HEIGHT - i - 1)] == player) count++;
        }
        if (count == HEIGHT) return true;
        
        return false;
    }
    
    // check number of rows / columns / diags for player with plays number of moves and no opponents
    // i.e. checkNumPlays(1, 2):
    //      how many rows / cols / diags are there with two Os and no Xs
    // used in the eval function
    public int checkNumPlays(int player, int plays) {
        int total = 0;
        int count;
        
        // check horizontals
        for (int i = 0; i < HEIGHT; i++) {
            count = 0;
            for (int j = 0; j < WIDTH; j++) {
                if (moves[getPositionForMove(j, i)] == player) count++;
                else {
                    count = 0;
                    break;
                }
            }
            
            if (count == WIDTH) total++;
        }
        
        // check verticals
        for (int i = 0; i < WIDTH; i++) {
            count = 0;
            for (int j = 0; j < HEIGHT; j++) {
                if (moves[getPositionForMove(i, j)] == player) count++;
                else {
                    count = 0;
                    break;
                }
            }
            
            if (count == HEIGHT) total++;
        }
        
        // check horizontals
        count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            if (moves[getPositionForMove(i, i)] == player) count++;
            else {
                count = 0;
                break;
            }
        }
        if (count == HEIGHT) total++;
        
        count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            if (moves[getPositionForMove(i, HEIGHT - i - 1)] == player) count++;
            else {
                count = 0;
                break;
            }
        }
        if (count == HEIGHT) total++;
        
        return total;
    }
    
    public Boolean checkTie() {
        int count = 0;
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            if (moves[i] != 0) count++;
        }
        return count == WIDTH * HEIGHT;
    }
    
    public ArrayList<BoardState> getPossibleMoves(int player) {
        ArrayList<BoardState> possibleMoves = new ArrayList<>();
        
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            if (checkMove(i)) {
                BoardState tempState = new BoardState(this);
                tempState.makeMove(player, i);
                possibleMoves.add(tempState);
            }
        }
        
        return possibleMoves;
    }
    
    // functions to print board to console
    public void printBoard() {
        printBoard(this);
    }
    
    public static void printBoard(BoardState boardState) {
        for (int i = 0; i < boardState.WIDTH * boardState.HEIGHT; i++) {
            if (i % boardState.WIDTH == 0) System.out.print("\n");
            System.out.print(boardState.moves[i] + " ");
        }
        
        System.out.print("\n");
    }
}
