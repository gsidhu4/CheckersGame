package ui;

import core.CheckersComputerPlayer;
import core.CheckersLogic;
import java.util.Scanner;

/** This class included the interaction that the user will have with this game. It will ask 
 * 	the player if they want to play PVP or against the CPU. It will run the game using methods 
 *  from CheckersLogic or CheckersComputerPlayer Class depending on what the user entered.
 * 
 * @author Gurbachan Sidhu
 * @version 1.1 06/03/2023
 *
 */

public class CheckersTextConsole {
	
	/** This is the main method where the user is greeted, asked what move they want to make, and the game continues to run in here.
	 * 
	 * @param args command line argument
	 * @throws Exception This exception handles if the user enters the wrong format when entering the move they want to make.
	 */
	
    public static void TextConsole()throws Exception {
    	
    	//create new game object and set up game
        CheckersLogic game = new CheckersLogic();
        CheckersComputerPlayer computerPlayer = new CheckersComputerPlayer('o');
        
        //print gameboard/greet user
        game.printBoard();
        System.out.println("Welcome to the Game of Checkers!");

        //ask if user wants to play against P or C
        Scanner scan = new Scanner(System.in);
        System.out.println("Do you want to play against the computer? (Enter Y for yes or N for no)");
        String choice = scan.nextLine();

        //gameloop that runs until gameover conditions met
        while (!game.gameOver()) {
        	
        	//if currentplayer is x
            if (game.getCurrent_player() == 'x') {
                System.out.println("Player X – your turn");
                System.out.println("Choose a cell position of piece to be moved and the new position. e.g., 3b-4a");

                try {
                	//get player's input for the move 
                    String input = scan.nextLine();
                    String[] positions = input.split("-");
                    String source = positions[0];
                    String destination = positions[1];
                    
                    //turning move entered to row and col indices 
                    int startRow = 8 - Integer.parseInt(source.substring(0, 1));
                    int startCol = source.charAt(1) - 'a';
                    int endRow = 8 - Integer.parseInt(destination.substring(0, 1));
                    int endCol = destination.charAt(1) - 'a';
                    
                    
                    //use indicies to determine if gamemove is valid or not
                    if (!game.isValidMove(startRow, startCol, endRow, endCol)) {
                        System.out.println("Invalid move! Please try again.");
                        continue; // if not valid, ask user again
                    }

                    game.makeMove(startRow, startCol, endRow, endCol);
                    game.setCurrent_player(game.getCurrent_player() == 'x' ? 'o' : 'x');

                    game.printBoard();
                } catch (Exception e) {
                    System.out.println("Invalid input format! Please try again.");// exception handled where wrong format is entered
                }
                
            } else {
            	
            	//if playing against computer 
                if (choice.equalsIgnoreCase("Y")) {
                    System.out.println("Computer O's Turn");
                    
                    //computer making move
                    int[] computerMove = computerPlayer.makeMove(game);
                    int startRow = computerMove[0];
                    int startCol = computerMove[1];
                    int endRow = computerMove[2];
                    int endCol = computerMove[3];

                    game.makeMove(startRow, startCol, endRow, endCol);
                    game.setCurrent_player(game.getCurrent_player() == 'x' ? 'o' : 'x');

                    game.printBoard();
                } else {
                	//if N was entered and playing PvP
                    System.out.println("Player O – your turn");
                    System.out.println("Choose a cell position of piece to be moved and the new position. e.g., 6a-5b");

                    try {
                    	//get players input for move
                        String input = scan.nextLine();
                        String[] positions = input.split("-");
                        String source = positions[0];
                        String destination = positions[1];
                        
                        //convert move entered to row and col indices 
                        int startRow = 8 - Integer.parseInt(source.substring(0, 1));
                        int startCol = source.charAt(1) - 'a';
                        int endRow = 8 - Integer.parseInt(destination.substring(0, 1));
                        int endCol = destination.charAt(1) - 'a';
                        
                        //use those row and col indices to determine if move is valid
                        if (!game.isValidMove(startRow, startCol, endRow, endCol)) {
                            System.out.println("Invalid move! Please try again.");
                            continue; //if not valid, make user enter again
                        }

                        game.makeMove(startRow, startCol, endRow, endCol);
                        game.setCurrent_player(game.getCurrent_player() == 'x' ? 'o' : 'x');

                        game.printBoard();
                    } catch (Exception e) {
                        System.out.println("Invalid input format! Please try again."); // exception handled where input for move is entered in wrong format.
                    }
                }
            }
        }
    }
}
