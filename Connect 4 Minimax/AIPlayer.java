import java.util.*;

/**
 * This is the AiPlayer class. It simulates a minimax player for the max connect
 * four game.
 * 
 */

public class AIPlayer {

	public static int maxplayer;
	public static int minplayer;

	/**
	 * This method depth limited mini-maxi (alpha-beta pruning) algorithm on the board
	 * 
	 * @param currentGame The GameBoard object that is currently being used to play
	 *                    the game.
	 * @return an integer indicating which column the AiPlayer would like to play
	 *         in.
	 */
	public int findBestPlay(GameBoard currentGame, int depth) {

		int choice = -1;
		int utility = Integer.MIN_VALUE;
		System.out.println("\nThinking...");

		// traverse through all the possible actions and find action with maximum
		// utility value
		for (int action : getActions(currentGame)) {

			int value = minValue(result(currentGame, action), utility, Integer.MAX_VALUE, depth - 1);

			if (value > utility) {
				utility = value;
				choice = action;
			}
		}
		return choice;
	}

	// gets the max value from children of state
	public int maxValue(GameBoard state, int alpha, int beta, int depth) {

		// terminal state
		if (depth == 0 || state.getPieceCount() == 42) {
			return utility(state);
		}

		int value = Integer.MIN_VALUE;

		for (int action : getActions(state)) {
			value = Math.max(value, minValue(result(state, action), alpha, beta, depth - 1));
			// beta pruning
			if (value >= beta) {
				return value;
			}
			alpha = Math.max(alpha, value);
		}
		return value;
	}

	// gets the min value from children of state
	public int minValue(GameBoard state, int alpha, int beta, int depth) {

		// terminal state
		if (depth == 0 || state.getPieceCount() == 42) {
			return utility(state);
		}

		int value = Integer.MAX_VALUE;

		for (int action : getActions(state)) {

			value = Math.min(value, maxValue(result(state, action), alpha, beta, depth - 1));
			// alpha pruning
			if (value <= alpha) {
				return value;
			}
			beta = Math.min(beta, value);
		}
		return value;
	}

	// eval function: calculates the utility value for the given state
	private int utility(GameBoard state) {

		int currentScore1 = state.getScore(1);
		int currentScore2 = state.getScore(2);

		int[][] temp1 = generatePossibleMoves(state, 1);
		int[][] temp2 = generatePossibleMoves(state, 2);

		int futureScore1 = new GameBoard(temp1).getScore(1);
		int futureScore2 = new GameBoard(temp2).getScore(2);

		// weighted score
		int score1 = currentScore1 * 5 + futureScore1 * 1;
		int score2 = currentScore2 * 5 + futureScore2 * 1;

		// calculate utility

		if (maxplayer == 1) {
			return score1 - score2;
		} else {
			return score2 - score1;
		}
	}

	// generates all possible future moves for player
	private int[][] generatePossibleMoves(GameBoard state, int player) {

		GameBoard temp = new GameBoard(state.getGameBoard());
		int[][] res = temp.getGameBoard();

		for (int i = 0; i < res.length; i++) {
			for (int j = 0; j < res[i].length; j++) {
				if (res[i][j] == 0) {
					res[i][j] = player;
				}
			}
		}
		return res;
	}

	// returns the resulting state from the action on the given state
	private GameBoard result(GameBoard state, int column) {

		GameBoard result = new GameBoard(state.getGameBoard());
		result.playPiece(column);
		return result;
	}

	// returns the list of valid actions for the given game state
	private List<Integer> getActions(GameBoard state) {

		List<Integer> res = new ArrayList<Integer>();

		for (int i = 0; i < 7; i++) {
			if (state.isValidPlay(i)) {
				res.add(i);
			}
		}
		return res;
	}
}