package core;

import java.util.ArrayList;
import java.util.List;

/** This class contains the logic for the computer player if the user chooses to play against
 *  a computer. The computer will make a move on the board based on the current available moves
 *  on the board and will only make the move if it is a valid move.
 * 
 * @author Gurbachan Sidhu
 * @version 1.0 06/03/2023
 */

public class CheckersComputerPlayer {
	// the gamemove that the computer will use
    private char playerSymbol;
    
    /** This a method that sets the computer players object with the specific player symbol.
     * 
     * @param playerSymbol This is the char either 'x' or 'o' that the computer will use.
     */

    public CheckersComputerPlayer(char playerSymbol) {
        this.playerSymbol = playerSymbol;
    }
    
    
    /** This method has the computer player make a move in the game based on the
     *  available moves.
     * 
     * @param game this is the current state of the checkers game
     * @return this is an array that has the startrow,startcol, endrow, endcol.
     */
    public int[] makeMove(CheckersLogic game) {
        int[][] availableMoves = getAllValidMoves(game);

        // having computer choose random move from avaiable valid moves
        int randomIndex = (int) (Math.random() * availableMoves.length);
        int[] move = availableMoves[randomIndex];

        return move;
    }
    /** This method will get all valid moves for the checkers computer player.
     * 
     * @param game this is the current state of the checkers game
     * @return this method returns a 2d array that contains all the valid moves. 
     */

    private int[][] getAllValidMoves(CheckersLogic game) {
        List<int[]> validMoves = new ArrayList<>();

        char currentPlayer = playerSymbol; // sets computer to current player
        
        //iterate over each cell position on gameboard	
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
            	
            	//does piece at given spot belong to com
                if (game.getPieceAtPosition(row, col) == currentPlayer) {
                	//if yes, generate list of those valid moves
                    List<int[]> moves = generateValidMoves(game, row, col);
                    validMoves.addAll(moves);
                }
            }
        }
        //converting list of moves to a 2D array
        int[][] movesArray = new int[validMoves.size()][];
        for (int i = 0; i < validMoves.size(); i++) {
            movesArray[i] = validMoves.get(i);
        }

        return movesArray;
    }
    
    /** This method generates a list of all valid moves for a computer player to make based on the current state of the
     *  checkers game.
     * 
     * @param game this is the current state of the checkers game
     * @param startRow the starting row for the piece that is being checked for valid moves
     * @param startCol the starting col for the piece that is being checked for valid moves
     * @return this method returns a list of all valid moves [startrow, startcol,endrow,endcol] for the computer to make.
     */

    private List<int[]> generateValidMoves(CheckersLogic game, int startRow, int startCol) {
        List<int[]> validMoves = new ArrayList<>();
        char currentPlayer = playerSymbol;

        // Check regular moves
        int[][] directions = { { 1, -1 }, { 1, 1 }, { -1, -1 }, { -1, 1 } };
        for (int[] direction : directions) {
            int newRow = startRow + direction[0];
            int newCol = startCol + direction[1];
            if (game.isValidMove(startRow, startCol, newRow, newCol)) {
                int[] move = { startRow, startCol, newRow, newCol };
                validMoves.add(move); // adding valid moves to the list
            }
        }

        // Check jump moves
        int[][] jumpDirections = { { 2, -2 }, { 2, 2 }, { -2, -2 }, { -2, 2 } };
        for (int[] direction : jumpDirections) {
            int newRow = startRow + direction[0];
            int newCol = startCol + direction[1];
            if (game.isValidMove(startRow, startCol, newRow, newCol)) {
                int capturedRow = (startRow + newRow) / 2;
                int capturedCol = (startCol + newCol) / 2;
                
                //does jumped piece belong to opponent
                if (game.getPieceAtPosition(capturedRow, capturedCol) == game.getOpponent(currentPlayer)) {
                    int[] move = { startRow, startCol, newRow, newCol };
                    validMoves.add(move); //adding validmoves to the list
                }
            }
        }

        return validMoves;
    }
}
