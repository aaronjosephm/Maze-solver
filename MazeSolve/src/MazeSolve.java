import java.util.Random;
import java.lang.StringBuilder;

class Maze {
    private static final int EAST = 0;
    private static final int SOUTH = 1;
    private static final int VISITED = 2;
    private static final int VIRGIN = 0;
    private static final int TOUCHED = 1;
    private static final int EXPLORED = 2;
    final int SIZE;
    final int FULL_SIZE;
    int[] main_cell_stack;
    int[][] maze;
    private static final String[] d = {"East", "South", "West", "North"};  // debug var

    void debug(String s) {
        System.out.print(s);
        try {
            Thread.sleep(500);
        }
        catch (Exception ignored) {}
    }

    Maze(int size) {
        SIZE = size;
        FULL_SIZE = SIZE*SIZE;
        maze = new int[4][FULL_SIZE];
        main_cell_stack = new int[FULL_SIZE];
    }

    void randomise() {
        Random rand = new Random(System.nanoTime());
        for (int col = 0; col < FULL_SIZE; col++) {
            // Bound on EAST
            if ((col + 1)%SIZE != 0)
                maze[EAST][col] = rand.nextInt(2);
            // Bound on SOUTH
            if (col < FULL_SIZE - SIZE)
                maze[SOUTH][col] = rand.nextInt(2);
        }
    }

    void mazify() {
        // Clear any visited data.
        for (int i = 0; i < maze[1].length; i++)
        {
            maze[VISITED][i] = VIRGIN;
        }

        int[] cell_stack = new int[FULL_SIZE];
        int stack_pointer = 0;
        int visited_cells = 1;
        int current_cell = 0;
        maze[VISITED][current_cell] = EXPLORED;

        while (visited_cells < FULL_SIZE) {
            int destination = pick_direction(current_cell);

            // If no good direction, pop.
            if (destination == -1) 
            {
                current_cell = cell_stack[stack_pointer--];
            }

            // Call the builders, knock the wall.
            else {
            	//destination represents the cell that you want to go to.
                if (destination < current_cell) {
                    // Cell to the WEST.
                    if (current_cell - destination == 1)
                        maze[EAST][destination] = 1;
                    else // NORTH.
                        maze[SOUTH][destination] = 1;
                }
                else {
                    // Cell to the EAST.
                    if (destination - current_cell == 1)
                        maze[EAST][current_cell] = 1;
                    else
                        maze[SOUTH][current_cell] = 1;
                }
                
                current_cell = destination;
                maze[VISITED][destination] = EXPLORED;
                cell_stack[++stack_pointer] = current_cell;
                visited_cells++;
            }
//            this.print(VISITED);
            try {
                Thread.sleep(3);
            }
            catch (Exception e) {}
        }
    }

    int pick_direction(int current) {
        Random rand = new Random(System.nanoTime());

        // Walls will be an array, holding four 1s or 0s, depending on the existence of those walls.
        // After that, for each existing wall, we check if we have a path.
        // If a path exists, we mark it as 0 (no wall).
        // Note that this is the reverse of how the original array is encoded.
        int[] walls = count_walls(current);
        for (int i = 0; i < walls.length; i++)
            if (walls[i] == 1)
                // Here i is direction. 0 = EAST, 1 = SOUTH, 2 = WEST, 3 = NORTH
            	if (is_connected(current, i))
                {
                    walls[i] = 0;
                }

        if ((walls[0] + walls[1] + walls[2] + walls[3]) < 1)
            return -1;

        // Randomly pick one of four directions.
        while (true) {
            int result = rand.nextInt(4);
            switch (result) {
                // EAST, make sure we're not at the edge.
                case 0 : if (walls[0] == 1) {
//                            debug("going: " + d[0] + "\n\n");
                            return current + 1;
                         }
                         break;
                // SOUTH
                case 1 : if (walls[1] == 1) {
//                            debug("going: " + d[1] + "\n\n");
                            return current + SIZE;
                         }
                         break;
                // WEST
                case 2 : if (walls[2] == 1) {
//                            debug("going: " + d[2] + "\n\n");
                            return current - 1;
                         }
                         break;
                // NORTH
                case 3 : if (walls[3] == 1) {
//                            debug("going: " + d[3] + "\n\n");
                            return current - SIZE;
                         }
                         break;
            }
            
        }
    }

    int[] count_walls(int current) {
        int[] result = new int[4];
        // EAST.
        if ((current + 1) % SIZE != 0)
            if (maze[EAST][current] == 0)
                result[0] = 1;
        // SOUTH.
        if (current + SIZE < FULL_SIZE)
            if (maze[SOUTH][current] == 0)
                result[1] = 1;
        // WEST.
        if (current % SIZE != 0)
            if (maze[EAST][current - 1] == 0)
                result[2] = 1;
        // NORTH.
        if (current - SIZE >= 0)
            if (maze[SOUTH][current - SIZE] == 0)
                result[3] = 1;
        return result;
    }

    boolean is_connected(int cell_a, int direction) {
//        debug("is_connected(" + cell_a + ", " + d[direction] + ") ");
        int cell_b = -1;
        if (direction == 0)
            cell_b = cell_a + 1;
        else if (direction == 1)
            cell_b = cell_a + SIZE;
        else if (direction == 2)
            cell_b = cell_a - 1;
        else if (direction == 3)
            cell_b = cell_a - SIZE;
        else {
            System.out.println("Error in is_connected. Direction was " + direction);
            System.exit(1);
        }
       
        boolean result = maze[VISITED][cell_b] == EXPLORED;
//        debug(result?"yes\n":"no\n");
        return result;
    }

    
    void print_debug() {
        StringBuilder output = new StringBuilder(SIZE*10);

        output.append("cell: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", col%10 + 1));
        output.append("\neast: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", maze[0][col]));
        output.append("\nsout: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", maze[1][col]));
        output.append("\nvisi: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", maze[2][col]));
        output.append("\ntrav: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", maze[3][col]));
        output.append("\n");

        System.out.println(output.toString());
    }

    void print(int mode) {
        StringBuilder output = new StringBuilder(SIZE*10);

        output.append("+   ");
        for (int col = 1; col < SIZE; col++)
            output.append("+---");
        output.append("+\n");

        for (int row = 0; row < SIZE; row++) {
            output.append("|");
            for (int col = 0; col < SIZE - 1; col++) {
                if (maze[EAST][row * SIZE + col] == 1)
                    output.append("    ");
                else
                    output.append("   |");
            }
            output.append("   |\n");

            if (row == SIZE - 1)
                break;

            output.append("+");
            for (int col = 0; col < SIZE; col++) {
                if (maze[SOUTH][row * SIZE + col] == 1)
                    output.append("   +");
                else
                    output.append("---+");
            }
            output.append("\n");
        }

        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+   +\n");

        System.out.println(output.toString());
        //print_debug();
    }

    void print_count() {
        StringBuilder output = new StringBuilder(SIZE*10);

        output.append("+   ");
        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+\n");

        for (int row = 0; row < SIZE; row++) {
            output.append("|");
            for (int col = 0; col < SIZE - 1; col++) {
                int[] walls = count_walls(row * SIZE + col);
                int count = walls[0] + walls[1] + walls[2] + walls[3];
                if (maze[EAST][row * SIZE + col] == 1)
                    output.append(" " + count + "  ");
                else
                    output.append(" " + count + " |");
            }
            int[] walls = count_walls((row + 1) * SIZE - 1);
            int count = walls[0] + walls[1] + walls[2] + walls[3];
            output.append(" " + count + " |\n");

            if (row == SIZE - 1)
                break;

            output.append("+");
            for (int col = 0; col < SIZE; col++) {
                if (maze[SOUTH][row * SIZE + col] == 1)
                    output.append("   +");
                else
                    output.append("---+");
            }
            output.append("\n");
        }

        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+   +\n");

        System.out.println(output.toString());
    }
    
    void DFS_solve()
    {
    	for (int i = 0; i < maze[1].length; i++)
    	{
            maze[VISITED][i] = VIRGIN;
            maze[3][i] = 0;
    	}
    	
    	//print_debug();

        int[] cell_stack = new int[FULL_SIZE];
        int stack_pointer = -1;
        int visited_cells = 1;
        int current_cell = 0;
        maze[VISITED][current_cell] = EXPLORED;

        while (current_cell < FULL_SIZE-1) {
            int destination = DFS_pick_direction(current_cell);

            // If no good direction, pop.
            if (destination == -1 &&  DFS_pick_direction(cell_stack[stack_pointer - 1]) == -1) 
            {
            	
                current_cell = cell_stack[stack_pointer];
                cell_stack[stack_pointer] = 0;
                stack_pointer--;
                //cell_stack[stack_pointer++] = 0;
                //stack_pointer++;
            }
            
            else if (destination == -1 && DFS_pick_direction(cell_stack[stack_pointer - 1]) != -1)
            {
            	current_cell = cell_stack[stack_pointer-1];
            	stack_pointer--;
                //cell_stack[stack_pointer] = 0;
                //stack_pointer--;
            }

            // Call the builders, knock the wall.
            else {
            	//destination represents the cell that you want to go to.
            	maze[3][destination] = visited_cells;
                
                current_cell = destination;
                maze[VISITED][destination] = EXPLORED;
                stack_pointer++;
                cell_stack[stack_pointer] = current_cell;
                visited_cells++;
            }
//            this.print(VISITED);
            try {
                Thread.sleep(3);
            }
            catch (Exception e) {}
        }
        
        for (int i = 0; i < cell_stack.length; i++)
        {
        	main_cell_stack[i] = cell_stack[i];
        	//System.out.print(" " + cell_stack[i] + " ");
        }
        //System.out.println(" ");
    }
    
    int DFS_pick_direction(int current) {
        Random rand = new Random(System.nanoTime());

        // Walls will be an array, holding four 1s or 0s, depending on the existence of those walls.
        // After that, for each existing wall, we check if we have a path.
        // If a path exists, we mark it as 0 (no wall).
        // Note that this is the reverse of how the original array is encoded.
        int[] walls = DFS_count_openings(current);
        for (int i = 0; i < walls.length; i++)
            if (walls[i] == 1)
                // Here i is direction. 0 = EAST, 1 = SOUTH, 2 = WEST, 3 = NORTH
            	if (DFS_is_traversed(current, i))
            	{
            		walls[i] = 0;
            	}

        if ((walls[0] + walls[1] + walls[2] + walls[3]) < 1)
            return -1;

        // Randomly pick one of four directions.
        while (true) {
            int result = rand.nextInt(4);
            switch (result) {
                // EAST, make sure we're not at the edge.
                case 0 : if (walls[0] == 1) {
//                            debug("going: " + d[0] + "\n\n");
                            return current + 1;
                         }
                         break;
                // SOUTH
                case 1 : if (walls[1] == 1) {
//                            debug("going: " + d[1] + "\n\n");
                            return current + SIZE;
                         }
                         break;
                // WEST
                case 2 : if (walls[2] == 1) {
//                            debug("going: " + d[2] + "\n\n");
                            return current - 1;
                         }
                         break;
                // NORTH
                case 3 : if (walls[3] == 1) {
//                            debug("going: " + d[3] + "\n\n");
                            return current - SIZE;
                         }
                         break;
            }
            
        }
    }

    int[] DFS_count_openings(int current) {
        int[] result = new int[4];
        // EAST.
        if ((current + 1) % SIZE != 0)
            if (maze[EAST][current] == 1 && maze[VISITED][current + 1] != EXPLORED)
                result[0] = 1;
        // SOUTH.
        if (current + SIZE < FULL_SIZE)
            if (maze[SOUTH][current] == 1 && maze[VISITED][current + SIZE] != EXPLORED)
                result[1] = 1;
        // WEST.
        if (current % SIZE != 0)
            if (maze[EAST][current - 1] == 1 && maze[VISITED][current - 1] != EXPLORED)
                result[2] = 1;
        // NORTH.
        if (current - SIZE >= 0)
            if (maze[SOUTH][current - SIZE] == 1 && maze[VISITED][current - SIZE] != EXPLORED)
                result[3] = 1;
        return result;
    }

    boolean DFS_is_traversed(int cell_a, int direction) {
//        debug("is_connected(" + cell_a + ", " + d[direction] + ") ");
        int cell_b = -1;
        if (direction == 0)
            cell_b = cell_a + 1;
        else if (direction == 1)
            cell_b = cell_a + SIZE;
        else if (direction == 2)
            cell_b = cell_a - 1;
        else if (direction == 3)
            cell_b = cell_a - SIZE;
        else {
            System.out.println("Error in is_connected. Direction was " + direction);
            System.exit(1);
        }
       
        boolean result = maze[VISITED][cell_b] == EXPLORED;
//        debug(result?"yes\n":"no\n");
        return result;
    }
    
    void DFS_print() {
    	StringBuilder output = new StringBuilder(SIZE*10);

        output.append("+   ");
        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+\n");

        for (int row = 0; row < SIZE; row++) {
            output.append("|");
            for (int col = 0; col < SIZE - 1; col++) {
                String count = Integer.toString(maze[3][(row*10) + col]);
                if (((row*10) + col) != 0 && maze[3][(row*10) + col] == 0)
				{
                	count = " ";
				}
                if (maze[EAST][row * SIZE + col] == 1)
                	if (maze[3][(row*10) + col] >= 10)
                	{
                		output.append(" " + count + " ");
                	}
                	else
                	{
                		output.append(" " + count + "  ");
                	}
                else
                {
                	if (maze[3][(row*10) + col] >= 10)
                	{
                		output.append(" " + count + "|");
                	}
                	else
                	{
                		output.append(" " + count + " |");
                	}
                }
            }
            String count = Integer.toString(maze[3][(row*10) + 9]);
            int temp = 0;
            if (((row*10) + 9) != 0 && maze[3][(row*10) + 9] == 0)
			{
            	count = " ";
            	temp++;
			}
            if (temp == 0 && Integer.parseInt(count) >= 10)
        	{
            	output.append(" " + count + "|\n");

        	}
        	else 
        	{
        		output.append(" " + count + " |\n");
        	}
            
            if (row == SIZE - 1)
                break;

            output.append("+");
            for (int col = 0; col < SIZE; col++) {
                if (maze[SOUTH][row * SIZE + col] == 1)
                    output.append("   +");
                else
                    output.append("---+");
            }
            output.append("\n");
        }

        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+   +\n");

        System.out.println(output.toString());
    }
    
    void DFS_print_solve()
    {
    	int temp = 1;
    	int count = 0;
    	for (int i = 0; i < main_cell_stack.length; i++)
    	{
    		System.out.print(main_cell_stack[i] + " ");
    		if (main_cell_stack[i] != 0)
    		{
    			temp++;
    		}
    	}
    	System.out.println();
    	String cells = new String(" ");
    	for (int i = 0; i < temp; i++)
    	{
    		if (main_cell_stack[i] < 10)
    		{
    			cells = cells + Integer.toString(main_cell_stack[i]) + ". ";
    		}
    		else
    		{
    			cells = cells + Integer.toString(main_cell_stack[i]) + " ";
    		}
    	}
    	System.out.println(cells);
    	StringBuilder output = new StringBuilder(SIZE*10);

        output.append("+   ");
        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+\n");

        for (int row = 0; row < SIZE; row++) {
            output.append("|");
            for (int col = 0; col < SIZE; col++) {
            	String common = Integer.toString(row*SIZE + col);
            	if ((row*SIZE + col) < 10)
            	{
            		common = Integer.toString(row*SIZE + col) + ".";
            	}
            	if (cells.contains(common) && maze[EAST][row * SIZE + col] == 1)
            	{
            		output.append(" #  ");
            		count++;
            	}
            	else if (cells.contains(common))
            	{
            		output.append(" # |");
            		count++;
            	}
            	else if (maze[EAST][row * SIZE + col] == 1)
            	{
                    output.append("    ");
            	}
                else
                    output.append("   |");
            }
            output.append("   \n");

            if (row == SIZE - 1)
                break;

            output.append("+");
            for (int col = 0; col < SIZE; col++) {
                if (maze[SOUTH][row * SIZE + col] == 1)
                    output.append("   +");
                else
                    output.append("---+");
            }
            output.append("\n");
        }

        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+   +\n");

        System.out.println(output.toString());
    }
}

class MazeSolve {
    public static void main(String[] args) {
        Maze maze = new Maze(10);
        //maze.randomise();
        maze.print(1);
        maze.mazify();
        maze.print(1);
        maze.DFS_solve();
        //maze.print_debug();
        //maze.print(1);
        maze.DFS_print();
        maze.DFS_print_solve();
        maze.print_debug();
        //maze.mazify();
        //maze.print_debug();
    }
}