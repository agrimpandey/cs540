import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// FILL THIS METHOD

		// explored list is a Boolean array that indicates if a state associated
		// with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()]
				[maze.getNoOfCols()];
		PriorityQueue<StateFValuePair> frontier = 
				new PriorityQueue<StateFValuePair>();

		// initialize the root state and add to frontier list
		Square start = super.maze.getPlayerSquare();
		State root = new State(start, null, 0, 0); 
		StateFValuePair first = new StateFValuePair(root, compute_fVal(root));
		frontier.add(first);
		maxSizeOfFrontier = 1;

		while (!frontier.isEmpty()) {

			// use frontier.poll() to extract the minimum stateFValuePair.
			StateFValuePair state_f_pair = frontier.poll();
			State state = state_f_pair.getState();

			// maintain the cost, noOfNodesExpanded 
			// (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			updateSearchInfo(state);
			explored[state.getX()][state.getY()] = true;

			// solution found
			if(state.isGoal(super.maze))
			{
				// print dots
				print_dots(state);
				return true;
			}
			
			
			ArrayList<State> neighbours = state.getSuccessors
					(explored, super.maze);
			Collections.reverse(neighbours);

			// add successor states to priority queue
			boolean isSame;
			boolean added;
			for(State counter_nb: neighbours)
			{
				isSame = false;
				added = false;
				for(StateFValuePair counter_ft: frontier)
				{

					if(counter_nb.isSame(counter_ft.getState()))
					{
						isSame = true;
						double fVal_n = compute_fVal(counter_nb);
						state_f_pair = new StateFValuePair(counter_nb, fVal_n);
						// only add if its fVal is lesser 
						if(state_f_pair.compareTo(counter_ft) == -1)
						{
							frontier.add(state_f_pair);
							added = true;
						}
						break;
					}
				}
				if(!isSame)
				{
					if (!added)
					{
						double fVal_n = compute_fVal(counter_nb);
						state_f_pair = new StateFValuePair(counter_nb, fVal_n);
						frontier.add(state_f_pair);
					}
				}
			}

			if(frontier.size() > maxSizeOfFrontier)
				maxSizeOfFrontier = frontier.size();
		}

		// return false if no solution
		return false;
	}

	private double compute_fVal(State s)
	{
		// return gVal + hVal
		return s.getGValue() + 
				Math.sqrt(
						Math.pow((s.getX()-maze.getGoalSquare().X),2) + 
						Math.pow((s.getY()-maze.getGoalSquare().Y),2)
						);
	}

	private void updateSearchInfo(State state)
	{
		noOfNodesExpanded++;
		if(state.getDepth() > maxDepthSearched)
			maxDepthSearched = state.getDepth();
		cost = state.getDepth();
	}
	
	private void print_dots(State s)
	{		
		// make sure there isn't a '.' in place of S and G
		if(!s.isGoal(super.maze) && 
				super.maze.getSquareValue(s.getX(), s.getY()) != 'S')
			super.maze.setOneSquare(s.getSquare(),'.');
		s = s.getParent();
		if(s != null)
			print_dots(s);
	}
}
