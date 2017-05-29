import static org.junit.Assert.*;
import org.junit.Test;


public class MazeSolveTester
{
	@Test
	public void Timetest()
	{
		Maze maze = new Maze(10);
		maze.mazify();
		
		//Time for DFS
		long startTime = System.currentTimeMillis();
		maze.DFS_solve();
        long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("DFS run time = " + totalTime + "ms");
		
		//Time for BFS
		startTime = System.currentTimeMillis();
		maze.DFS_solve();
        endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println("BFS run time = " + totalTime + "ms");
	}
	
	@Test
	public void compare()
	{
		Maze maze = new Maze(10);
		
		maze.mazify();
		
		maze.DFS_solve();
		
		String DFS_stack = new String();
		
		for (int i = 0; i < maze.main_cell_stack.length; i++)
		{
			if (maze.main_cell_stack[i] < 10)
			{
				DFS_stack = Integer.toString(maze.main_cell_stack[i]) + ". ";
			}
			else
			{
				DFS_stack = Integer.toString(maze.main_cell_stack[i]) + " ";
			}
		}
		
		maze.DFS_solve();
		
		//now the maze 2d array is changed because of BFS solve.
		int count = 0;
		for (int i = 0; i < maze.main_cell_stack.length; i++)
		{
			if (maze.main_cell_stack[i] < 10)
			{
				String temp = Integer.toString(maze.main_cell_stack[i]) + ". ";
				if (!DFS_stack.contains(temp))
				{
					System.out.println("DFS and BFS have different paths");
					count++;
				}
			}
			else
			{
				String temp = Integer.toString(maze.main_cell_stack[i]) + " ";
				if (!DFS_stack.contains(temp))
				{
					System.out.println("DFS and BFS have different paths");
					count++;
				}
			}
		}
		
		if (count == 0)
		{
			System.out.println("DFS and BFS paths are equal!");
		}
		
		
	}
	
}

