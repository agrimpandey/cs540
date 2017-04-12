import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// FILL THIS METHOD

		// explored list is a 2D Boolean array that indicates if a state 
		// associated with a given position in the maze has already been 
		// explored.
		boolean[][] explored = new boolean
				[maze.getNoOfRows()][maze.getNoOfCols()];

		// Stack implementing the Frontier list
		LinkedList<State> stack = new LinkedList<State>();
		Square start_square = super.maze.getPlayerSquare();
		State start_state = new State(start_square, null, 0, 0); 		
		stack.push(start_state);
		maxSizeOfFrontier = 1;

		// START LOOP
		while (!stack.isEmpty()) {	
			// update the maze if a solution found
			State new_state = stack.pop();

			// maintain the cost, noOfNodesExpanded 
			// (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			updateSearchInfo(new_state);
			explored[new_state.getX()][new_state.getY()] = true;

			// solution found
			if(new_state.isGoal(super.maze))
			{
				// print dots
				print_dots(new_state);
				return true;
			}

			ArrayList<State> neighbours = new_state.getSuccessors
					(explored, super.maze);
			
			// add successor states to stack
			boolean isSame;
			for(State counter_nb: neighbours)
			{
				isSame = false;
				for(State counter_st: stack)
				{
					if(counter_nb.isSame(counter_st))
					{
						isSame = true;
						break;
					}
				}
				if(!isSame)
					stack.push(counter_nb);
			}
			if(stack.size() > maxSizeOfFrontier)
				maxSizeOfFrontier = stack.size();
		}

		// return false if no solution
		return false;
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
