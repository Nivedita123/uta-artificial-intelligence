import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MaxConnect4 {

	public static void main(String[] args) {

		// check for the correct number of arguments
		if (args.length != 4) {

			System.out.println("Four command-line arguments are needed:\n");
			exit_function(0);
		}

		// parse the input arguments
		String game_mode = args[0].toString();
		String inputFile = args[1].toString();
		int depthLevel = Integer.parseInt(args[3]);

		// create and initialize the game board
		GameBoard currentGame = new GameBoard(inputFile);

		// create the Ai Player
		AIPlayer ai = new AIPlayer();

		System.out.println("***** MaxConnect-4 Game *****");

		if (game_mode.equalsIgnoreCase("interactive")) {

			String player = args[2].toString();

			// if arg is valid
			if (player.equals("human-next") || player.equals("computer-next")) {

				System.out.println("Game state before move:");

				// print the current game board
				currentGame.printGameBoard();

				// keep playing till no more moves are left
				while (currentGame.getPieceCount() < 42) {

					if (player.equals("computer-next")) {

						// setting maxplayer and minplayer id based on input from file
						if (currentGame.getCurrentTurn() == 1) {
							AIPlayer.maxplayer = 1;
							AIPlayer.minplayer = 2;
						} else {
							AIPlayer.maxplayer = 2;
							AIPlayer.minplayer = 1;
						}

						// print the current scores
						System.out.println("Scores: Computer = " + currentGame.getScore(AIPlayer.maxplayer)
								+ ", Human = " + currentGame.getScore(AIPlayer.minplayer));

						System.out.println("My Chance:");
						// choose a move
						int moveColumn = ai.findBestPlay(currentGame, depthLevel);

						// play the piece
						currentGame.playPiece(moveColumn);

						player = "human-next";

					} else {

						if (currentGame.getCurrentTurn() == 1) {
							AIPlayer.maxplayer = 2;
							AIPlayer.minplayer = 1;
						} else {
							AIPlayer.maxplayer = 1;
							AIPlayer.minplayer = 2;
						}

						// print the current scores
						System.out.println("Scores: Computer = " + currentGame.getScore(AIPlayer.maxplayer)
								+ ", Human = " + currentGame.getScore(AIPlayer.minplayer));

						System.out.println("Your Chance:");

						// keep asking till user enters a valid move
						while (true) {

							System.out.println("Enter your move:");

							// read user input and handle invalid input
							try {

								BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
								int moveColumn = Integer.parseInt(reader.readLine());

								if (currentGame.isValidPlay(moveColumn)) {

									currentGame.playPiece(moveColumn);
									break;

								} else {

									System.out.println("Invalid move! try again!");
								}
							} catch (Exception e) {

								System.out.println("Invalid move! try again!");
							}
						}

						// move made, computer's turn now
						player = "computer-next";
					}

					System.out.println("Game state after move:");
					currentGame.printGameBoard();
					// print the current scores
					System.out.println("Score: Computer = " + currentGame.getScore(AIPlayer.maxplayer) + ", Human = "
							+ currentGame.getScore(AIPlayer.minplayer) + "\n ");

				}

				System.out.println("No more moves left! Game over!");

				if (currentGame.getScore(AIPlayer.maxplayer) > currentGame.getScore(AIPlayer.minplayer)) {
					System.out.println("Computer WINS!!");
				} else if (currentGame.getScore(AIPlayer.maxplayer) < currentGame.getScore(AIPlayer.minplayer)) {
					System.out.println("Human WINS!!");
				} else {
					System.out.println("DRAW!!");
				}

			} else {

				System.out.println("Invalid player!");
			}
		}

		// one move mode
		else if (game_mode.equalsIgnoreCase("one-move")) {

			// get the output file name
			String output = args[2].toString();

			System.out.println("Game state before move:\n");

			// print the current game board
			currentGame.printGameBoard();

			// print the current scores
			System.out
					.println("Score: Player 1 = " + currentGame.getScore(1) + ", Player2 = " + currentGame.getScore(2));

			// empty space is available on the game board
			if (currentGame.getPieceCount() < 42) {

				int currentPlayer = currentGame.getCurrentTurn();

				// set maxplayer/minplayer id based on input
				if (currentPlayer == 1) {

					AIPlayer.maxplayer = 1;
					AIPlayer.minplayer = 2;

				} else {

					AIPlayer.maxplayer = 2;
					AIPlayer.minplayer = 1;
				}

				// make a choice
				int moveColumn = ai.findBestPlay(currentGame, depthLevel);

				// play the piece
				currentGame.playPiece(moveColumn);

				// display the current game board
				System.out.println(
						"Move " + currentGame.getPieceCount() + ": Player " + currentPlayer + ", column " + moveColumn);

				System.out.println("Game state after move:");
				currentGame.printGameBoard();

				// print the current scores
				System.out.println(
						"Scores: Player 1 = " + currentGame.getScore(1) + ", Player 2 = " + currentGame.getScore(2));

				currentGame.printGameBoardToFile(output);

			} else {

				System.out.println("I can't play. The Board is Full. Game Over!!");

				if (currentGame.getScore(AIPlayer.maxplayer) > currentGame.getScore(AIPlayer.minplayer)) {
					System.out.println("You LOOSE!!");
				} else if (currentGame.getScore(AIPlayer.maxplayer) < currentGame.getScore(AIPlayer.minplayer)) {
					System.out.println("You WIN!!");
				} else {
					System.out.println("DRAW!!");
				}
			}

		} else {

			System.out.println("\n" + game_mode + " is an unrecognized game mode.\nTry again1 \n");
			return;
		}
	}

	/**
	 * This method is used when to exit the program prematurely.
	 * 
	 * @param value an integer that is returned to the system when the program
	 *              exits.
	 */
	private static void exit_function(int value) {
		System.out.println("exiting from MaxConnectFour.java!\n\n");
		System.exit(value);
	}
}