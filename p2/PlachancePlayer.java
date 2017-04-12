/****************************************************************
 * studPlayer.java
 * Implements MiniMax search with A-B pruning and iterative deepening search (IDS). The static board
 * evaluator (SBE) function is simple: the # of stones in studPlayer's
 * mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */




//################################################################
// studPlayer class
//################################################################

public class PlachancePlayer extends Player {


	//I made move basically just a infinite loop that calls maxAction over and over with increasing maxDepths
	//The IDS will 	 be implemented through the max depth (max Action calls min action calsl max action... until
	//maxDepth is reached then returns all the way back updating move, and incrementing maxDepth and starting
	//the loop over.
	//The actual minimax search mechanics will be implemented through minAction and maxAction, and the sbe method
	//will generate the values for the nodes.  This is where the subjective value assigning comes in.  We can decide
	//how we want to do this probably last, since it shouldn't be too important.  We can just make something basic like
	//number of stones in your end minus the opposing end to start and make it more complex if we have time


	//note psuedo code for this can be found in the february 10 notes

	/*Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
	 * interrupted by the time limit. The best move found in each step should be stored in the
	 * protected variable move of class Player.
	 */
	public void move(GameState state)
	{

		int maxDepth = 1;
		while(true)
		{
			move = maxAction(state, maxDepth);
			maxDepth++;
		}
	} 
	// Return best move for max player. Note that this is a wrapper function created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the best move by comparing the 
	// sbe values returned by maxSBE. This function should call minAction with 5 parameters.
	public int maxAction(GameState state, int maxDepth)
	{
		GameState temp_state = new GameState(state);
		int actionNumber = 0;
		int v = Integer.MIN_VALUE;
		int a = Integer.MIN_VALUE;
		int b = Integer.MAX_VALUE;
		for(int i  =  0; i <= 5; i++)
		{
			if(temp_state.stoneCount(i) == 0)
				continue;
			int s;
			if(temp_state.applyMove(i))
				s = maxAction(temp_state, 1, maxDepth, a, b);
			else
				s = minAction(temp_state, 1, maxDepth, a , b);
			if(s > v)
			{
				v = s;
				actionNumber = i;
			}
			a = Math.max(a, v);
			if(b <= a)
			{
				temp_state = new GameState(state);
				break;
			}
			temp_state = new GameState(state);
		}
		return actionNumber;

	}

	//return sbe value related to the best move for max player
	public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
	{
		if (currentDepth == maxDepth || state.gameOver())
			return sbe(state);

		GameState temp_state =  new GameState(state);

		int v = Integer.MIN_VALUE;
		for(int i =0; i < 6; i++)
		{
			if(temp_state.stoneCount(i) == 0)
				continue;

			if(temp_state.applyMove(i))
				v = Math.max(v, maxAction(temp_state, currentDepth, maxDepth, alpha, beta));
			else
				v = Math.max(v, minAction(temp_state, currentDepth + 1, maxDepth, alpha, beta));
			
			alpha = Math.max(alpha, v);
			
			if (beta <= alpha)
			{
				temp_state = new GameState(state);
				break;
			}
			temp_state = new GameState(state);

		}

		return v;

	}
	//return sbe value related to the bset move for min player
	public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
	{

		if(currentDepth == maxDepth || state.gameOver())
			return sbe(state);

		GameState temp_state =  new GameState(state);

		int v = Integer.MAX_VALUE;
		for(int i =7; i < 13; i++)
		{
			if(temp_state.stoneCount(i) == 0)
				continue;

			if(temp_state.applyMove(i))
				v = Math.min (v, minAction(temp_state, currentDepth, maxDepth, alpha, beta));
			else
				v = Math.min(v, maxAction(temp_state, currentDepth + 1, maxDepth, alpha, beta));
			
			beta = Math.min(beta, v);
			if (beta <= alpha)
			{
				temp_state = new GameState(state);
				break;
			}
			temp_state = new GameState(state);
		}
		return v;

	}

	//the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
	private int sbe(GameState state)
	{
		
		int[] bins = state.toArray();
		int sbe = bins[6] - bins[13];
     	return sbe;
	}  
}

