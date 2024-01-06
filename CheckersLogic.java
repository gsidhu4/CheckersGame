package core;

/** This class represents the logic behind the Checkers Board
 *  game. The size of the game board, printing the game board,making
 *  a move on the board, checking for a winner in the game, etc. is all
 *  included in this class.
 *
 *
 * @author Gurbachan Sidhu
 * @version 1.1 06/03/2023
 *
 */

public class CheckersLogic {

    //2d array of gameboard
    public char[][] gameboard;

    //current player move
    public char gamemove;

    //number of xcheckers
    private int oCheckers = 12;

    //number of ocheckers
    private int xCheckers = 12;
    
 
  
    
    
    /** CheckersLogic creates the 8x8 gameboard that the game will be played on. 
     *  The board will have 12 x pieces and 12 o pieces and CheckersLogic places those 
     *  pieces in the appropriate spot.In addition, the methods to determine a validmove, 
     *  to move a piece, and game end conditions are in this class.
     *    
     */
    
    
    public CheckersLogic() {
        //setting current players move to x and initalizing gameboard
        gameboard = new char[8][8];
        gamemove = 'x';
        initalizeBoard();
    }
    
    /**  getter method to get current players char
     * 
     * @return current_player. This will return either player x or o dependign on the current players move
     */

    public char getCurrent_player() {
        //getter for currentplayer
        return gamemove;
    }
    
    /** Setter method to set current player
     * 
     * @param current_player. This is the player whos turn it is.
     */

    public void setCurrent_player(char current_player) {
        //setter for current player
        this.gamemove = current_player;
    }
    
    
    /** This method intializes the gameboard placing x's and o's in correct spots.
     * 
     */
    
    public void initalizeBoard() {

        gameboard = new char[8][8];


        //iterating through 2d array and placing x's and o's on the board
        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                if (row % 2 == col % 2) {

                    if (row < 3)
                        gameboard[row][col] = 'o';
                    else if (row > 4)
                        gameboard[row][col] = 'x';
                    else
                        gameboard[row][col] = '_';
                } else {
                    gameboard[row][col] = '_';
                }
            }
        }
    }
    
    /** printBoard prints the gameboard that was just defined above. It iterates and prints numbers 8-1 decreasing order on 
     *  the side vertically and prints a-h horizontally underneath the gameboard.
     */

    public void printBoard() {
        //printing from 8-1 vertically on left side
        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + " | ");
            for (int j = 0; j < 8; j++) {
                //prints x's and o's on the board and the board itself
                System.out.print(gameboard[i][j] + " | ");
            }
            System.out.println();
        }
        //printing a-h horizontally below board
        System.out.println("    a   b   c   d   e   f   g   h");
    }
    
    /** The following method takes in the following parameters and decides whether a move is valid to make in the game.
     * 
     * @param startRow This is the index of the row of the starting pos.
     * @param startCol This is the index of the col of the starting pos.
     * @param endRow   This is the index of the row of the ending pos.
     * @param endCol   This is the index of the col of the ending pos.
     * @return         This method will return true or false. True if the move is valid, false if 
     * 			       the move is not valid.
     * @throws ArrayIndexOutOfBoundsException This exception will handle any case when there are elements trying to be 
     * 										  accessed outside of the gameboard.  
     */

    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) throws ArrayIndexOutOfBoundsException {
        try {
            // if statement return false if out of bounds move
            if (startRow < 0 || startRow >= 8 || startCol < 0 || startCol >= 8 ||
                    endRow < 0 || endRow >= 8 || endCol < 0 || endCol >= 8) {
                return false;
            }

            // if target pos is not empty ret false
            if (gameboard[endRow][endCol] != '_') {
                return false;
            }

            // check if move is going forward and diagonally
            char currentPlayer = gameboard[startRow][startCol];
            if ((currentPlayer == 'x' && endRow >= startRow) || (currentPlayer == 'o' && endRow <= startRow)) {
                return false;
            }

            // is the move a regualar move or jump move
            int rowDiff = Math.abs(endRow - startRow);
            int colDiff = Math.abs(endCol - startCol);

            //regular move if diff is only 1
            if (rowDiff == 1 && colDiff == 1) {
                return true;

            } else if (rowDiff == 2 && colDiff == 2) {
                // Jump move if diff is 2
                int capturedRow = (startRow + endRow) / 2;
                int capturedCol = (startCol + endCol) / 2;
                char capturedPiece = gameboard[capturedRow][capturedCol];


                if (capturedPiece == getOpponent(currentPlayer)) {
                    return true;
                }
            }

            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Handle the exception when accessing elements outside the board array
            return false;
        }
    }
    
    /** The following method uses the following parameters to move a checkers piece in the game.
     * 
     * @param startRow This is the index of the row of the starting pos.
     * @param startCol This is the index of the col of the starting pos.
     * @param endRow   This is the index of the row of the ending pos.
     * @param endCol   This is the index of the col of the ending pos.
     *
     * @throws ArrayIndexOutOfBoundsException This exception will handle any case when there are elements trying to be 
     * 										  accessed outside of the gameboard.  
     */
    
    

    public void makeMove(int startRow, int startCol, int endRow, int endCol)throws ArrayIndexOutOfBoundsException {
        if (!isValidMove(startRow, startCol, endRow, endCol)) {
            System.out.println("Invalid move! Please try again.");
            return;
        }

        try {
            char currentPlayer = gameboard[startRow][startCol];
            gameboard[endRow][endCol] = currentPlayer;
            gameboard[startRow][startCol] = '_';

            // Checking if it is a jump move
            int rowDiff = Math.abs(endRow - startRow);
            int colDiff = Math.abs(endCol - startCol);
            if (rowDiff == 2 && colDiff == 2) {


                // removes piece that was jumped
                int capturedRow = (startRow + endRow) / 2;
                int capturedCol = (startCol + endCol) / 2;
                gameboard[capturedRow][capturedCol] = '_';
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Handle the exception when accessing elements outside the board array
            System.out.println("Invalid move! Please try again.");
        }
      
    }
    
    /**
     * Checks if the game is over (one player has no checkers or cannot make any moves).
     *
     * @return True or False. It will return True if game is over, otherwise it will return False.
     */

  
    public boolean gameOver() {
        //boolean variables
        boolean xCheckerExists = false;
        boolean oCheckerExists = false;
        boolean xCanMove = false;
        boolean oCanMove = false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (gameboard[row][col] == 'x') {
                    xCheckerExists = true;
                    if (canPieceMove(row, col)) {
                        xCanMove = true;
                    }
                } else if (gameboard[row][col] == 'o') {
                    oCheckerExists = true;
                    if (canPieceMove(row, col)) {
                        oCanMove = true;
                    }
                }
            }
        }
        //end game conditions
        if (!xCheckerExists || !xCanMove) {
            System.out.println("Player O wins!");
            return true;
        } else if (!oCheckerExists || !oCanMove) {
            System.out.println("Player X wins!");
            return true;
        }
        //player x has no more checkers
        if (xCheckers == 0) {
            System.out.print("Player O Wins!");
            return true;
        }
        // player o has no more checkers
        if (oCheckers == 0) {
            System.out.print("Player X Wins!");
            return true;
        }

        return false; // game is not over
    }
    
    /** This method will check if a piece at the given position can make any valid moves.
     *
     * @param row The row index of the piece in question.
     * @param col The column index of the piece in question.
     * @return True if the piece can move, false otherwise.
     * @throws ArrayIndexOutOfBoundsException This exception will handle any case when there are elements trying to be 
     * 										  accessed outside of the gameboard.  
     */
    
    public boolean canPieceMove(int row, int col) throws ArrayIndexOutOfBoundsException {
        try {
            char piece = gameboard[row][col];
            char opponent = getOpponent(piece);

            // Check for regular moves
            if (isValidMove(row, col, row + 1, col + 1) ||
                    isValidMove(row, col, row + 1, col - 1) ||
                    isValidMove(row, col, row - 1, col + 1) ||
                    isValidMove(row, col, row - 1, col - 1)) {
                return true;
            }

            // Check for jump moves
            if (isValidMove(row, col, row + 2, col + 2) ||
                    isValidMove(row, col, row + 2, col - 2) ||
                    isValidMove(row, col, row - 2, col + 2) ||
                    isValidMove(row, col, row - 2, col - 2)) {
                return true;
            }

            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Handle the exception when accessing elements outside the board array
            return false;
        }
    }
    
    /** This method gets the opponent player of the player entered.
     * 
     * @param player The player who's opponent is going to be returned.
     * @return The opponenet player either 'x' or 'o'
     */

    char getOpponent(char player) {
        //returns opponent of any given player
        return player == 'x' ? 'o' : 'x';
    }
    
    /** This method will retrieve the piece at the given position on the board.
     *
     * @param row The row index of the position.
     * @param col The column index of the position.
     * @return The piece at the position.
     * @throws ArrayIndexOutOfBoundsException This exception will handle any case when there are elements trying to be 
     * 										  accessed outside of the gameboard.  
     */
    public char getPieceAtPosition(int row, int col)throws ArrayIndexOutOfBoundsException  {
        try {
            return gameboard[row][col];
        } catch (ArrayIndexOutOfBoundsException e) {
            // Handle the exception when accessing elements outside the board array
            return ' ';
        }
    }
}

 




