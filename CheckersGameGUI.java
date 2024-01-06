package ui;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.Optional;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.*;
import core.CheckersLogic;
import core.CheckersComputerPlayer;



/** This CheckersGameGUI class represents the graphical user interface for the checkers game.
 * 
 * @author Gurbachan Sidhu
 * @version 1.0 06/07/2023
 *
 */
public class CheckersGameGUI extends Application {
    
	//variables defining the square size on the checkerboard
    public static final int squareSize = 75;
    
    //8 square wide
    public static final int WIDTH = 8;
    //8 squares height
    public static final int HEIGHT = 8;
    
    // instane of CheckersLogic class
    CheckersLogic gLogic = new CheckersLogic();
    
    //2D array gameboard using Tile object
    private Tile[][] gameboard = new Tile[WIDTH][HEIGHT];

    //Group for Tiles on gameboard and checker pieces on the board
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    
    // keeping track of player turn
    private boolean redTurn = true;
    
    //winnerLabel for displaying winner
    private Label winnerLabel = new Label();
    
    //turnlabel for displaing player turn
    private Label turnLabel = new Label();


   /**
    *  The initializeBoard method initializes the game board and returns a Parent node containing all the graphical elements.
    *
    *  @return A Parent node containing the game board and other graphical elements.
    */
    
    private Parent initializeBoard() {
    	// create pane
        Pane root = new Pane();
       
 
        root.setStyle("-fx-background-color: #ff0000;");
  
        
        Text text = new Text();
        text.setText("Welcome to ASU's official Checker Game!!"
        		+ " To Play: Drag the piece to appropriate location to make a move. It is red's turn first! Have Fun!!");
        text.setX(200);
        text.setY(666);
        text.setFont(Font.font("Helvetica", 25));
        text.setFill(Color.YELLOW);
        
        Image image = new Image("ASUlogo.jpg");
        ImageView imageview = new ImageView(image);
        imageview.setX(700);
        imageview.setY(700);
        
        Image devil = new Image("devil.jpg");
        ImageView imageview2 = new ImageView(devil);
        imageview2.setX(800);
        imageview2.setY(30);
        
        

        //setting size of pane
        root.setPrefSize(600, 600);
        root.getChildren().addAll(tileGroup, pieceGroup, winnerLabel,turnLabel,text,imageview,imageview2);
        
        
        
        //pos winner label
        winnerLabel.relocate(WIDTH * squareSize / 2 - 50, HEIGHT * squareSize + 10);
        winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        winnerLabel.setTextFill(Color.BLACK);
        
        //iterate over gameboard
        for (int y = 0; y < HEIGHT; y++) { 
            for (int x = 0; x < WIDTH; x++) {
            	// new tile and pos
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                
                //tile stored in gameboard array
                gameboard[x][y] = tile;
                // tile added to tilegroup
                tileGroup.getChildren().add(tile);

                Piece piece = null;
                
                //place red pieces on the board
                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = generatePiece(PieceType.RED, x, y);
                }
                
                //place white pieces on the board
                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = generatePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        return root;// returns pane with all elements on it
    }
    
   /**
    * Validates a move for a given piece to the specified coordinates on the game board.
    *
    * @param piece The piece to be moved.
    * @param xvar  The x-coordinate of the destination.
    * @param yvar  The y-coordinate of the destination.
    * @return A MoveResult object indicating the type of move and any additional actions that should be taken.
    */

    private MoveResult validateMove(Piece piece, int xvar, int yvar) {
    	//checking if there is a piece already at xvar and yvar 
        if (gameboard[xvar][yvar].hasPiece() || (xvar + yvar) % 2 == 0) {
        	//returns no move if there is
            return new MoveResult(typeOfMove.NONE);
        }
        //checks if it is the right players turn
        if ((piece.getPieceType() == PieceType.RED && !redTurn) ||
                (piece.getPieceType() == PieceType.WHITE && redTurn)) {
        	//if wrong player tries to move, result is nothing is moved
            return new MoveResult(typeOfMove.NONE);
        }
        //starting pos of checker piece
        int startX = convertPixelToBoardCoordinate(piece.getStartX());
        int startY = convertPixelToBoardCoordinate(piece.getStartY());
        
        // normal move calculated 
        if (Math.abs(xvar - startX) == 1 && yvar - startY == piece.getPieceType().direction) {
            return new MoveResult(typeOfMove.NORMAL);
            
            //jump move calculated
        } else if (Math.abs(xvar - startX) == 2 && yvar - startY == piece.getPieceType().direction * 2) {
        	
        	//jump pieced position
            int jumpedPieceX = startX + (xvar - startX) / 2;
            int jumpedPieceY = startY + (yvar - startY) / 2;
            
            //checking if an opponents piece was at the position
            if (gameboard[jumpedPieceX][jumpedPieceY].hasPiece() && gameboard[jumpedPieceX][jumpedPieceY].getPiece().getPieceType() != piece.getPieceType()) {
                
            	
            	// if so, results in removing that piece
            	return new MoveResult(typeOfMove.JUMP, gameboard[jumpedPieceX][jumpedPieceY].getPiece());
            }
        }
        
        
        return new MoveResult(typeOfMove.NONE); // if no other conditions met
    }
    
   /**
    * Converts a pixel coordinate to the corresponding board coordinate.
    *
    * @param pixel The pixel coordinate to be converted.
    * @return The board coordinate.
    */

    private int convertPixelToBoardCoordinate(double pixel) {
        return (int)(pixel + squareSize / 2) / squareSize;
    }
    
   /**
    * Starts the Checkers game application.
    *
    * @param primaryStage The primary stage for the application.
    * @throws Exception If an exception occurs during the start of the application.
    */

    public void start(Stage primaryStage) throws Exception {
    	// asking user if they want to play using GUI or text UI
        boolean useGUI = showPlayOptionDialog(); 
        //if GUI clicked
        if (useGUI ) {
            Scene scene = new Scene(initializeBoard());
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("Press Esc to exit full screen mode");
            primaryStage.setTitle("CheckersGame");
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
        	//if Text UI clicked
            CheckersTextConsole.TextConsole();
        }
    }
    
   /**
    * Shows a dialog box to the user asking to choose the game mode.
    *
    * @return true if the user chooses GUI mode and wants to play against an opponent, false if the user chooses text-based UI mode.
    */
    private boolean showPlayOptionDialog() {
    	// alert asking game mode from user
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Options");
        alert.setHeaderText("Choose Game Mode");
        alert.setContentText("Please choose the game mode:");

        // two buttons one for each game mode
        ButtonType guiButton = new ButtonType("GUI");
        ButtonType textButton = new ButtonType("Text-based UI");
        
        //adding buttons to dialog box
        alert.getButtonTypes().setAll(guiButton, textButton);
        
        //waiting for user click
        Optional<ButtonType> result = alert.showAndWait();
        
        // if player made a choice
        if (result.isPresent()) {
            ButtonType chosenOption = result.get();
            //if that choice is gui then opponenet question asked 
            if (chosenOption == guiButton) {
                return showOpponentOption();
            } else if (chosenOption == textButton) {
                return false;
            }
        }

        return false; // no option chose
    }
    
   /**
    * Shows a dialog box to the user asking to choose the opponent.
    *
    * @return true if the user chooses to play against another player, false if the user chooses to play against the computer.
    */

    private boolean showOpponentOption() {
    	// alert asking opponent choice from user
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Options");
        alert.setHeaderText("Choose Opponent");
        alert.setContentText("Please choose the opponent:");
        
        //two button choices. player or computer
        ButtonType playerButton = new ButtonType("Player v Player");
        ButtonType computerButton = new ButtonType("Player v Computer");
        
        //add buttons to dialog box
        alert.getButtonTypes().setAll(playerButton, computerButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent(); // was a choice made or not
    }
    
   /**
    * Generates a new piece of the specified type at the given coordinates on the gameboard.
    *
    * @param type The type of the piece (RED or WHITE).
    * @param x The x-coordinate of the piece on the gameboard.
    * @param y The y-coordinate of the piece on the gameboard.
    * @return The newly generated piece.
    */

    private Piece generatePiece(PieceType type, int x, int y) {
    	//creating a new piece and the pos of that piece
        Piece piece = new Piece(type, x, y);
        // event listener for when piece is released
        piece.setOnMouseReleased(e -> {
            int newX = convertPixelToBoardCoordinate(piece.getLayoutX());
            int newY = convertPixelToBoardCoordinate(piece.getLayoutY());
            
            
            MoveResult result = validateMove(piece, newX, newY);
            
            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(typeOfMove.NONE);
            } else {
                result = validateMove(piece, newX, newY);
            }
            //convert to board coordinates
            int oldX = convertPixelToBoardCoordinate(piece.getStartX());
            int oldY = convertPixelToBoardCoordinate(piece.getStartY());
            
            //switch case for different scenarios
            switch (result.getType()) {
            case NONE:
                piece.abortMove(); //if move is invalid, abort
                break;
            case NORMAL: // normal case where a valid move is made
                piece.move(newX, newY);
                gameboard[oldX][oldY].setPiece(null);
                gameboard[newX][newY].setPiece(piece);
                checkForWinner();
                showWinner();
                redTurn = !redTurn;// Switch the turn to the next player
                endGame();
                break;
            case JUMP: // case where a jump move is made
                piece.move(newX, newY);
                gameboard[oldX][oldY].setPiece(null);
                gameboard[newX][newY].setPiece(piece);
                Piece opponentPiece = result.getPiece();
                gameboard[convertPixelToBoardCoordinate(opponentPiece.getStartX())][convertPixelToBoardCoordinate(opponentPiece.getStartY())].setPiece(null);
                pieceGroup.getChildren().remove(opponentPiece);
                checkForWinner();
                showWinner();
                redTurn = !redTurn; // Switch the turn to the next player
                endGame();
                break;
        }

        });

        return piece;
    }
    
   /**
    * Checks for a winner in the game.
    *
    * @return An optional value representing the winner (RED or WHITE) if there is one, or an empty optional if the game is not yet over or it's a draw.
    */
    private Optional<PieceType> checkForWinner() {
    	//boolean variables for red and white pieces, if they have pieces or have moves available
        boolean redHasPieces = false;
        boolean whiteHasPieces = false;
        boolean redHasMoves = false;
        boolean whiteHasMoves = false;
        
        //iterate through gameboard
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
            	//piece at current poos
                Piece piece = gameboard[x][y].getPiece();
                //make sure there is a piece
                if (piece != null) {
                	
                //if piece is red
                    if (piece.getPieceType() == PieceType.RED) {
                        redHasPieces = true; // return true
                        if (hasValidMove(piece)) {
                            redHasMoves = true; // if hasvalidmove,return true
                        }
                    } else {
                    	//if piece is white
                        whiteHasPieces = true; //return true
                        if (hasValidMove(piece)) {
                            whiteHasMoves = true; //if hasvalidmove, return true
                        }
                    }
                }
            }
        }

        if (!redHasPieces && !whiteHasPieces) {
            return Optional.ofNullable(null); // If both players have no remaining pieces, it's a draw
        } else if (!redHasPieces && !redHasMoves) {
            return Optional.of(PieceType.WHITE); // White wins if no more red pieces or no available moves for red
        } else if (!whiteHasPieces && !whiteHasMoves) {
            return Optional.of(PieceType.RED); // Red wins if no more white pieces or no available moves for white
        }else {
            if (redHasMoves && !whiteHasMoves) {
                return Optional.of(PieceType.RED); // Red wins if only white has no available moves
            } else if (!redHasMoves && whiteHasMoves) {
                return Optional.of(PieceType.WHITE); // White wins if only red has no available moves
            } else {
                return Optional.empty(); // If both players still have pieces or available moves, or the game is not yet over, return empty optional
            }
        }
    }
    
   /**
    * Checks if the game is over.
    *
    * @return true if the game is over, false otherwise.
    */

    private boolean isGameOver() {
    	//variables to keep track of red and white pieces either for quantity or movement
        boolean redPiecesRemaining = false;
        boolean whitePiecesRemaining = false;
        boolean redCanMove = false;
        boolean whiteCanMove = false;

        // loop to see if pieces remaining for either player
        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 0; y < WIDTH; y++) {
                Piece piece = gameboard[y][x].getPiece();
                if (piece != null) {
                    if (piece.getPieceType() == PieceType.RED) {
                        redPiecesRemaining = true;
                    } else {
                        whitePiecesRemaining = true;
                    }
                }
            }
        }
        

        // Check if the current player can make a move
        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 0; y < WIDTH; y++) {
                Piece piece = gameboard[y][x].getPiece();
                if (piece != null) {
                    if ((piece.getPieceType() == PieceType.RED && redTurn) ||
                        (piece.getPieceType() == PieceType.WHITE && !redTurn)) {
                        // Check if the piece can make any valid moves
                        if (canMove(piece)) {
                            if (piece.getPieceType() == PieceType.RED) {
                                redCanMove = true;
                            } else {
                                whiteCanMove = true;
                            }
                        }
                    }
                }
            }
        }

        // Game over if either player is out of pieces or unable to make a move
        return (!redPiecesRemaining || !whitePiecesRemaining || (!redCanMove && redTurn) || (!whiteCanMove && !redTurn));
    }
    
   /**
    * Ends the game and displays the winner or a draw message.
    * If a winner is present, displays the winning player.
    * If the game is over with no winner, displays a draw message.
    * Resets the game state after displaying the result.
    */
    private void endGame() {
        Optional<PieceType> winner = checkForWinner();
        //if a winner is present, then displays the following
        if (winner.isPresent()) {
            String winnerText = winner.get() == PieceType.RED ? "Red" : "White";
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Winner");
            alert.setContentText(winnerText + " wins!");
            alert.showAndWait();
            resetGame();
        } else if (isGameOver()) {
        	//if game is over and there is no winner, then it is a draw.
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over");
            alert.setContentText("The game is over!!");
            alert.showAndWait();
            resetGame();
        }
    }
    
   /**
    * Resets the game state, clears the board, and enables pieces.
    * Sets the initial game conditions, including the turn, piece group, and labels.
    * Places red and white pieces on the board according to their initial positions.
    */

    private void resetGame() {
        // Reset the game state, clear the board, and enable pieces
        redTurn = true;
        pieceGroup.getChildren().clear();
        winnerLabel.setVisible(false);
        turnLabel.setVisible(false);
        
        
        // go through gameboard
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Piece piece = null;
                
                //place a red piece in appropriate spot
                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = generatePiece(PieceType.RED, x, y);
                }
                //place a white piece in appropriate spots
                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = generatePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                	gameboard[x][y].setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
    }
    
   /**
    * Checks if a given piece has any valid moves available.
    * 
    * @param piece The piece to check for valid moves.
    * @return True if the piece has valid moves available, false otherwise.
    */
    private boolean canMove(Piece piece) {
    	
        // First, get current pos of the piece on the game board.
        int x = convertPixelToBoardCoordinate(piece.getLayoutX());
        int y = convertPixelToBoardCoordinate(piece.getLayoutY());

        // define possible directions in which the piece can move
        // For red pieces, they can move down-left and down-right.
        // For white pieces, they can move up-left and up-right.
        int[][] directions = piece.getPieceType() == PieceType.RED ? new int[][]{{1, -1}, {1, 1}} : new int[][]{{-1, -1}, {-1, 1}};

        // Now, we check each possible direction to see if there is a valid move for the piece.
        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];

            // calculate end pos after moving in direction
            int newX = x + dx;
            int newY = y + dy;

            // If the calculated move is valid, we return true.
            if (isValidMove(piece, newX, newY)) {
                return true;
            }

            // We also check for possible "jump" moves, where the piece can skip over another piece.
            // We calculate the new position after moving two steps in the current direction.
            newX = x + (2 * dx);
            newY = y + (2 * dy);

            // returns true if jump move is valid
            if (isValidMove(piece, newX, newY)) {
                return true;
            }
        }

        // otherwise false
        return false;
    }
    
   /**
    * Displays the winner of the game.
    * Checks for a winner and displays the appropriate message if a winner is present.
    * If no winner is found, updates the turn label.
    */
    private void showWinner() {
    	//check for winner
        Optional<PieceType> winner = checkForWinner();
        //if winner is present, display appropriate message
        if (winner.isPresent()) {
            String winnerText = winner.get() == PieceType.RED ? "Red" : "White";
            winnerLabel.setText(winnerText + " wins!");
            winnerLabel.setVisible(true);
            turnLabel.setText(""); // Reset turn label when there's a winner
            disablePieces();
        } else {
        	//if no winner, turn is updated
            String turnText = redTurn ? "White" : "Red";
            turnLabel.setText("Turn: " + turnText);
            turnLabel.setFont(Font.font("Times New Roman", 15));;
            turnLabel.setVisible(true);
            winnerLabel.setVisible(false); // No winner, hide the label
            
            // in the case no one has available moves
            if (!hasAvailableMoves()) {
                winnerLabel.setText("Stalemate!");
                winnerLabel.setVisible(true);
                turnLabel.setText(""); // Reset turn label when there's a stalemate
                disablePieces();
            }
        }
    }
    
   /**
    * Checks if the current player has any available moves.
    * 
    * Returns true if there is at least one available move, false otherwise.
    */
    private boolean hasAvailableMoves() {
    	//iterate through positions on the gameboard
    	
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
            	//getpiece at current pos
                Piece piece = gameboard[x][y].getPiece();
                //if there is a piece at the current pos and it belongs to current player
                if (piece != null && ((redTurn && piece.getPieceType() == PieceType.RED) || (!redTurn && piece.getPieceType() == PieceType.WHITE))) {
                	//check if piece has available move
                    if (hasValidMove(piece)) {
                        return true; // valid move available
                    }
                }
            }
        }
        return false; // otherwise false
    }
    
   /**
    * Checks if a given piece has any valid moves available.
    *
    * @param piece The piece to check for valid moves.
    * @return true if the piece has valid moves, false otherwise.
    */
    private boolean hasValidMove(Piece piece) {
    	
    	//get initial position of piece 
        int x = convertPixelToBoardCoordinate(piece.getStartX());
        int y = convertPixelToBoardCoordinate(piece.getStartY());
        
        // directions in which piece can possibly move
        int[][] directions = piece.getPieceType() == PieceType.RED ? new int[][] { { 1, -1 }, { 1, 1 } }
                : new int[][] { { -1, -1 }, { -1, 1 } };
        //check each direction to see if there is a valid move for the piece        
        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            
            //new pos after moving in the current direction
            int newX = x + dx;
            int newY = y + dy;
            
            //if calculated move is valid, then true
            if (isValidMove(piece, newX, newY)) {
                return true;
            }
            //calculate possible jump moves
            newX = x + (2 * dx);
            newY = y + (2 * dy);
            
            //if valid, return true
            if (isValidMove(piece, newX, newY)) {
                return true;
            }
        }

        return false; // otherwise false
    }
    
   /**
    * Checks if a move is valid for a given piece and target position.
    * 
    * @param piece The piece attempting to make the move.
    * @param newX The X-coordinate of the target position.
    * @param newY The Y-coordinate of the target position.
    * @return true if the move is valid, false otherwise.
    */
    private boolean isValidMove(Piece piece, int newX, int newY) {
    
            // check if the new pos is within the gameboard
            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                return false;
            }

            // target where the piece is ending on gameboard 
            Tile target = gameboard[newX][newY];

            // check if target has a piece already and empty
            
            if (!target.hasPiece() && (newX + newY) % 2 != 0) {
            	
                //inital pos of the piece 
                int startX = convertPixelToBoardCoordinate(piece.getStartX());
                int startY = convertPixelToBoardCoordinate(piece.getStartY());

                // calculate the difference between new pos and starting pos
                int dx = Math.abs(newX - startX);
                int dy = Math.abs(newY - startY);

                // is the move a regular move or jump move
                if (dx == 1 && dy == 1 && newY - startY == piece.getPieceType().direction) {
                    
                    return true; // true for regular move 
                } else if (dx == 2 && dy == 2 && newY - startY == piece.getPieceType().direction * 2) {
                  
                	//calculate the tile that was jumped
                    int startX2 = startX + (newX - startX) / 2;
                    int startY2 = startY + (newY - startY) / 2;

                    //jumped tile and the piece on that tile
                    Tile middleTile = gameboard[startX2][startY2];
                    Piece middlePiece = middleTile.getPiece();

                    //if that piece is the other player, then jump is valid
                    if (middlePiece != null && middlePiece.getPieceType() != piece.getPieceType()) {
                        return true;
                    }
                }
            }

            // move is not satisfy any conditions 
            return false;
 
    }
    
   /**
    * Disables mouse interaction for the pieces belonging to the current player.
    * 
    */
    private void disablePieces() {
    	//determine the piece who is the current player
        PieceType currentPlayer = redTurn ? PieceType.WHITE : PieceType.RED;
        
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
            	//piece on current tile
                Piece piece = gameboard[x][y].getPiece();
                // if piece is on current pos and belongs to current player 
                if (piece != null && piece.getPieceType() == currentPlayer) {
                    piece.setOnMouseReleased(null); // Disable mouse interaction for the piece
                }
            }
        }
    }
    /**
     * Represents the result of a move made in the game.
     * 
     */
    public class MoveResult {

        private typeOfMove type; // type of move made
        private Piece piece; // represents a piece 
        
        /** getter for type of move made.
         * 
         * @return type which is the type of move made.
         */
        public typeOfMove getType() {
        	//getter method for getting type of move
            return type;
        }
        /** getter for piece involved
         * 
         * @return piece which is the piece involved
         */

        public Piece getPiece() {
        	//getter method to get the piece involved
            return piece;
        }
        
       /**
        * Constructs a MoveResult object with the specified type of move.
        *
        * @param type The type of move made.
        */

        public MoveResult(typeOfMove type) {
        	//constructor for type of move made
            this.type = type;
        }
        
       /**
        * Constructs a MoveResult object with the specified type of move and the piece involved.
        *
        * @param type  The type of move made.
        * @param piece The piece involved in the move.
        */
        public MoveResult(typeOfMove type, Piece piece) {
        	//constructor for type of move and piece involved 
            this.type = type;
            this.piece = piece;
        }
    }
    /**
     * Enum representing different types of moves.
     * It includes the options of no move, normal move, and jump move.
     */
    public enum typeOfMove {
    	
    	// represet a no move, normal move, and jump move
        NONE, NORMAL, JUMP
    }
    /**
     * Enum representing the type of a game piece.
     * It includes the options of RED and WHITE, with their corresponding directions.
     */
    public enum PieceType {
        RED(1), WHITE(-1);

        final int direction;
        
       /**
        * Constructs a PieceType enum with the specified direction.
        *
        * @param direction The direction of the piece.
        */

        PieceType(int direction) {
            this.direction = direction;
        }
    }
    
   /**
    * This class represents a game piece on the game board.
    * It extends StackPane to provide graphical representation and interaction.
    */
  
    public class Piece extends StackPane {

        private PieceType type; // variable for type of piece red or white
        private double mouseX, mouseY;  // variables for position
        private double startX, startY; // variables for start position

        /**
         * Retrieves the type of the piece.
         *
         * @return The type of the piece.
         */
        public PieceType getPieceType() {
            return type; // getter for piece type
        }

        /**
         * Retrieves the starting X position of the piece.
         *
         * @return The starting X position of the piece.
         */
        public double getStartX() {
            return startX; // getter for starting pos
        }

        /**
         * Retrieves the starting Y position of the piece.
         *
         * @return The starting Y position of the piece.
         */
        public double getStartY() {
            return startY; // getter for starting pos
        }

        
        /**
         * Constructs a new Piece with the specified type and position.
         *
         * @param type The type of the piece (red or white).
         * @param x    The X position of the piece.
         * @param y    The Y position of the piece.
         */
        public Piece(PieceType type, int x, int y) {
            this.type = type; // piece type

            move(x, y); // moving piece to that specific pos
          
            //creating ellipse that represents the checker
            Ellipse checker = new Ellipse(squareSize * 0.30, squareSize * 0.28);
            checker.setFill(type == PieceType.RED ? Color.RED : Color.WHITE);

            checker.setStroke(Color.BLACK);
            checker.setStrokeWidth(squareSize * 0.05);

            checker.setTranslateX((squareSize - squareSize * 0.3125 * 2) / 2);
            checker.setTranslateY((squareSize - squareSize * 0.26 * 2) / 2);

            getChildren().addAll(checker); // added checker 
            
            //inital positions when mouse pressed
            setOnMousePressed(e -> {
                mouseX = e.getSceneX();
                mouseY = e.getSceneY();
            });
            
            // relocate piece based on where its dragged
            setOnMouseDragged(e -> {
                relocate(e.getSceneX() - mouseX + startX, e.getSceneY() - mouseY + startY);
            });
        }
        
        /**
         * Moves the piece to the specified position.
         *
         * @param x The X position to move the piece to.
         * @param y The Y position to move the piece to.
         */

        public void move(int x, int y) {
            startX = x * squareSize; // starting x pos 
            startY = y * squareSize; // starting y pos
            relocate(startX, startY); // relocating using those two
        }
        
        /**
         * Aborts the current move and puts the piece back to its initial position.
         */

        public void abortMove() {
            relocate(startX, startY); // puts piece back in inital pos
        }
    }
    
    /**
     * This class represents a tile on the game board.
     * It extends Rectangle to provide graphical representation.
     */
    
    public class Tile extends Rectangle {

        private Piece piece; // variable to store piece  

        /**
         * Checks if the tile has a piece.
         *
         * @return true if the tile has a piece, false otherwise.
         */
        
        public boolean hasPiece() {
            return piece != null; // checking if tile has a piece
        }
        
        /**
         * Retrieves the piece on the tile.
         *
         * @return The piece on the tile.
         */

        public Piece getPiece() {
            return piece; // getting piece on tile
        }
        
        /**
         * Sets the piece on the tile.
         *
         * @param piece The piece to be set on the tile.
         */
        public void setPiece(Piece piece) {
            this.piece = piece; //setting piece on tile
        }
        
        /**
         * Constructs a new Tile with the specified color and position.
         *
         * @param light Indicates whether the tile is light or dark in color.
         * @param x     The X position of the tile.
         * @param y     The Y position of the tile.
         */

        public Tile(boolean light, int x, int y) {
            setWidth(CheckersGameGUI.squareSize); // setting width of tile 
            setHeight(CheckersGameGUI.squareSize); //setting height of tile
            //pos of tile is set based on x and y sizes
            relocate(x * CheckersGameGUI.squareSize, y * CheckersGameGUI.squareSize);
            
            //changing color of tile
            setFill(light ? Color.ANTIQUEWHITE : Color.BLACK);
        }
    }
    
    /**
     * The main entry point for the application.
     * Launches the Checkers game application.
     *
     * @param args The command line arguments passed to the application.
     */

    public static void main(String[] args) {
        launch(args); //launching app
    }

}
