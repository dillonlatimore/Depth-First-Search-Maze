
/*
 * Name			: 		Dillon Latimore
 * File 		: 		MazeGenerator.java
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class MazeGenerator{
    static Coord startCoord;
    static Coord finishCoord;
    static Random r = new Random();
    static boolean lastNotAdjacent = false;
    static int numOfRows = 0;
    static int numOfCols = 0;

    // Setup direction vectors
    static int dRow[] = { 0, 1, 0, -1 };
    static int dCol[] = { -1, 0, 1, 0 };

    // Keeps maze coordinate information
    private static class Coord
    {
        public int row;
        public int col;

        public Coord(int row, int col)
        {
            this.row = row;
            this.col = col;
        }
    }

    // Keeps required info about MazeCells
    private static class MazeCell
    {
        public int directionIn;
        public int walls;
        public int neighbours;
        public boolean isStart;
        public boolean isFinish;

        public MazeCell()
        {
            this.directionIn = -1;
            this.walls = -1;
            this.neighbours = 3;
            this.isStart = false;
            this.isFinish = false;
        }
    }

    // Main method
    public static void main(String[] args)
    {
        // Take number of rows and columns from command line
        numOfRows = Integer.parseInt(args[0]);
        numOfCols = Integer.parseInt(args[1]);
        String fileName = args[2];

        // Grid keeps numbers of each maze cell
        int grid[][] = new int[numOfRows][numOfCols];
        int val = 1;
        for(int i = 0; i < numOfRows; i++)
        {
            for(int j = 0; j < numOfCols; j++)
            {
                grid[i][j] = val;
                val++;
            }
        }

        // Keeps track of visited cells
        Boolean vis[][] = new Boolean[numOfRows][numOfCols];
        for(int i = 0; i < numOfRows; i++)
        {
            for(int j = 0; j < numOfCols; j++)
            {
                vis[i][j] = false;
            }
        }

        // Keeps info of each maze cell
        MazeCell maze[][] = new MazeCell[numOfRows][numOfCols];
        for(int i = 0; i < numOfRows; i++)
        {
            for(int j = 0; j < numOfCols; j++)
            {
                maze[i][j] = new MazeCell();
            }
        }

        // Randomly set start coordinates
        int startRow = r.nextInt(numOfRows);
        int startCol = r.nextInt(numOfCols);
        startCoord = new Coord(startRow, startCol);

        // Performs depth first search to create maze
        DFS(startCoord.row, startCoord.col, grid, vis, maze);
        printMaze(maze);
        makeOutputFile(maze, fileName, grid);
    }

    private static void makeOutputFile(MazeCell[][] maze, String fileName, int[][] grid) {
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
            } else {
              System.out.println("File already exists.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }

          
          String cellOpennessList = "";

          for(int i = 0; i < numOfRows; i++) {
            for(int j = 0; j < numOfCols; j++) {
                cellOpennessList += maze[i][j].walls;
            }
        }

        String outputString = numOfRows + "," + numOfCols + ":" + grid[startCoord.row][startCoord.col]
            + ":" + grid[finishCoord.row][finishCoord.col] + ":" + cellOpennessList;

          try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(outputString);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        


    }

    private static Boolean isValid(Boolean vis[][], int row, int col)
    {

        // If cell is out of bounds
        if (row < 0 || col < 0 || row >= numOfRows || col >= numOfCols) {
            return false;
        }


        // If the cell is already visited
        if (vis[row][col]) {
            return false;
        }


        // Otherwise, it can be visited
        return true;
    }

    // Function to perform DFS
    private static void DFS(int row, int col, int grid[][],
                    Boolean vis[][], MazeCell maze[][])
    {

        // Setup random list of coordinates
        ArrayList<Integer> randList = new ArrayList<Integer>();
        for(int i = 0; i <=3; i++) {
            randList.add(i);
        }

        // setup visited list
        ArrayList<Coord> visitOrderList = new ArrayList<Coord>();

        // Initialize a stack of coords and push the starting cell into it
        Stack<Coord> st = new Stack<Coord>();
        st.push(new Coord(row, col));

        // Set visit count
        int visitCount = 0;

        // Set current to first value on stack
        Coord current = st.peek();
        Coord last = null;

        // Iterate until the stack is not empty
        while (!st.empty())
        {
            // Pop the top coord
            current = st.pop();

            // Check if the current popped cell is valid or not if so then continue
            if (!isValid(vis, current.row, current.col)) {
                continue;
            }

            // If first cell set as start cell for maze
            if(visitCount == 0) {
                maze[current.row][current.col].isStart = true;
            }

            // Mark the current cell as visited
            vis[current.row][current.col] = true;

            // Add to visit order list
            visitOrderList.add(current);
            visitCount++;

            // Check if it is the last cell
            if(visitCount == numOfRows*numOfCols) {
                // set last as last adjacend cell
                last = findLastAdjacent(current, last, visitOrderList);
                // Set cell neighbours
                setNeighbour(current, last, maze);
                // Set cell direction in
                setDirectionIn(current, last, maze);
                // Build maze walls of last cell
                buildWalls(current, last, maze, visitOrderList);
                // Set current cell as finish cell
                maze[current.row][current.col].isFinish = true;
                // Set finish coordinate
                finishCoord = new Coord(current.row, current.col);
                // Build walls of final cell
                buildWalls(current, last, maze, visitOrderList);
                lastNotAdjacent = false;
            }

            // If not the first cell or the last cell
            if(last != null && visitCount != numOfRows*numOfCols) {
                // Set last 
                last = findLastAdjacent(current, last, visitOrderList);
                // Set neighbours and direction in
                setNeighbour(current, last, maze);
                setDirectionIn(current, last, maze);
                // Build walls of last cell
                buildWalls(current, last, maze, visitOrderList);
                lastNotAdjacent = false;
            }

            // Set current cell as last cell for iteration
            last = current;

            // Creates a random order of adjacent cell vectors to create maze randomly
            // and push those 4 cells to the stack
            int randomElement;
            for(int i = 0; i < 4; i++)
            {
                randomElement = randList.remove(r.nextInt(randList.size()));
                int adjx = current.row + dRow[randomElement];
                int adjy = current.col + dCol[randomElement];
                st.push(new Coord(adjx, adjy));
            }
            randList.clear();
            for(int i = 0; i <=3; i++) {
                randList.add(i);
            }
        }
    }

    // Sets the neighbour of the current cell or last cell depending on what direction
    // the maze is going next
    private static void setNeighbour(Coord current, Coord last, MazeCell[][] maze) {
        // If last cell was above
        if(last.row == current.row - 1 && last.col == current.col) {
            // If cell already has a right neighbour
            if(maze[last.row][last.col].neighbours == 1) {
                // Set neighbours as right and down
                maze[last.row][last.col].neighbours = 2;
            } else {
                // IF NO RIGHT NEIGHBOUR
                // SET NEIGHBOUR TO DOWN
                maze[last.row][last.col].neighbours = 0;
            }
        }

        // If last cell was to the left
        else if(last.col == current.col-1 && last.row == current.row) {
            // If cell already has neighbour below
            if(maze[last.row][last.col].neighbours == 0) {
                // Set neighbours to right and down
                maze[last.row][last.col].neighbours = 2;
            } else {
                // Set neighbour to right
                maze[last.row][last.col].neighbours = 1;
            }
        }

        // If last cell was below
        else if(last.row == current.row+1 && last.col == current.col) {
            // Set neighbour to below
            maze[current.row][current.col].neighbours = 0;
        }

        // If last cell was to the right
        else if(last.col == current.col+1 && last.row == current.row) {
            // Set neighbour to right
            maze[current.row][current.col].neighbours = 1;
        }

        else {
            //System.out.println("LAST CELL IS NOT ADJACENT");
        }
    }

    // Sets the direction in of a maze cell
    private static void setDirectionIn(Coord current, Coord last, MazeCell[][] maze) {
        // If going down
        if(last.row == current.row - 1) {
            maze[current.row][current.col].directionIn = 2;
        } 
        // If going up
        else if(last.row == current.row + 1){
            maze[current.row][current.col].directionIn = 0;
        } 
        // If going right
        else if(last.col == current.col - 1){
            maze[current.row][current.col].directionIn = 3;
        } 
        // If going left
        else if(last.col == current.col + 1){
            maze[current.row][current.col].directionIn = 1;
        }
    }

    // Builds the maze walls
    private static void buildWalls(Coord current, Coord last, MazeCell[][] maze, ArrayList<Coord> visitOrderList) {
         // If cell is last cell in maze
        if(maze[current.row][current.col].isFinish) {
            // If last cell is not adjacent to current cell
            if(lastNotAdjacent) {
                // Direction in: UP
                if (maze[current.row][current.col].directionIn == 0) {
                    // Build different maze walls depending on what cells are next to the current cell
                    if (maze[last.row][last.col].neighbours == 0) {
                        maze[last.row][last.col].walls = 2;
                    } else if (maze[last.row][last.col].neighbours == 1) {
                        maze[last.row][last.col].walls = 1;
                    } else if (maze[last.row][last.col].neighbours == 2) {
                        maze[last.row][last.col].walls = 3;
                    } else if (maze[last.row][last.col].neighbours == 3) {
                        maze[last.row][last.col].walls = 0;
                    }
                }

                // Direction in: LEFT
                else if (maze[current.row][current.col].directionIn == 1) {
                    if (maze[last.row][last.col].neighbours == 0) {
                        maze[last.row][last.col].walls = 2;
                    } else if (maze[last.row][last.col].neighbours == 1) {
                        maze[last.row][last.col].walls = 1;
                    } else if (maze[last.row][last.col].neighbours == 2) {
                        maze[last.row][last.col].walls = 3;
                    } else if (maze[last.row][last.col].neighbours == 3) {
                        maze[last.row][last.col].walls = 0;
                    }
                }

                // Direction in: DOWN
                else if (maze[current.row][current.col].directionIn == 2) {
                    if (maze[last.row][last.col].neighbours == 0) {
                        maze[last.row][last.col].walls = 2;
                    } else if (maze[last.row][last.col].neighbours == 1) {
                        maze[last.row][last.col].walls = 3;
                    } else if (maze[last.row][last.col].neighbours == 2) {
                        maze[last.row][last.col].walls = 3;
                    } else if (maze[last.row][last.col].neighbours == 3) {
                        maze[last.row][last.col].walls = 2;
                    }
                }

                // Direction in: RIGHT
                else if (maze[current.row][current.col].directionIn == 3) {
                    if (maze[last.row][last.col].neighbours == 0) {
                        maze[last.row][last.col].walls = 3;
                    } else if (maze[last.row][last.col].neighbours == 1) {
                        maze[last.row][last.col].walls = 1;
                    } else if (maze[last.row][last.col].neighbours == 2) {
                        maze[last.row][last.col].walls = 3;
                    } else if (maze[last.row][last.col].neighbours == 3) {
                        maze[last.row][last.col].walls = 1;
                    }
                }
            }

            // Direction in: UP
            if(maze[current.row][current.col].directionIn == 0) {
                maze[current.row][current.col].walls = 2;
            }
            // Direction in: LEFT
            else if(maze[current.row][current.col].directionIn == 1) {
                maze[current.row][current.col].walls = 1;
            }
            // Direction in: DOWN
            else if(maze[current.row][current.col].directionIn == 2) {
                maze[current.row][current.col].walls = 0;
            }
            // Direction in: RIGHT
            else if(maze[current.row][current.col].directionIn == 3) {
                maze[current.row][current.col].walls = 0;
            }
        }

        // If last cell wasn't adjacent but not the final cell
        else if(lastNotAdjacent) {
            // Sets walls of last cell even though it wasn't adjacent
            // Direction in: UP
            if(maze[visitOrderList.get(visitOrderList.size()-2).row][visitOrderList.get(visitOrderList.size()-2).col]
                    .directionIn == 0) {
                maze[visitOrderList.get(visitOrderList.size()-2).row][visitOrderList.get(visitOrderList.size()-2).col]
                        .walls = 2;
            }
            // Direction in: LEFT
            else if(maze[visitOrderList.get(visitOrderList.size()-2).row]
                    [visitOrderList.get(visitOrderList.size()-2).col].directionIn == 1) {
                maze[visitOrderList.get(visitOrderList.size()-2).row][visitOrderList.get(visitOrderList.size()-2).
                        col].walls = 1;
            }
            // Direction in: DOWN
            else if(maze[visitOrderList.get(visitOrderList.size()-2).row][visitOrderList.get(visitOrderList.size()-2)
                    .col].directionIn == 2) {
                maze[visitOrderList.get(visitOrderList.size()-2).row][visitOrderList.get(visitOrderList.size()-2).col]
                        .walls = 0;
            }
            // Direction in: RIGHT
            else if(maze[visitOrderList.get(visitOrderList.size()-2).row][visitOrderList.get(visitOrderList.size()-2).
                    col].directionIn == 3) {
                maze[visitOrderList.get(visitOrderList.size()-2).row][visitOrderList.get(visitOrderList.size()-2).col]
                        .walls = 0;
            }

            // Direction in: UP
            if (maze[current.row][current.col].directionIn == 0) {
                if (maze[last.row][last.col].neighbours == 0) {
                    maze[last.row][last.col].walls = 2;
                } else if (maze[last.row][last.col].neighbours == 1) {
                    maze[last.row][last.col].walls = 1;
                } else if (maze[last.row][last.col].neighbours == 2) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 3) {
                    maze[last.row][last.col].walls = 0;
                }
            }

            // Direction in: LEFT
            else if (maze[current.row][current.col].directionIn == 1) {
                if (maze[last.row][last.col].neighbours == 0) {
                    maze[last.row][last.col].walls = 2;
                } else if (maze[last.row][last.col].neighbours == 1) {
                    maze[last.row][last.col].walls = 1;
                } else if (maze[last.row][last.col].neighbours == 2) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 3) {
                    maze[last.row][last.col].walls = 0;
                }
            }

            // Direction in: DOWN
            else if (maze[current.row][current.col].directionIn == 2) {
                if (maze[last.row][last.col].neighbours == 0) {
                    maze[last.row][last.col].walls = 2;
                } else if (maze[last.row][last.col].neighbours == 1) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 2) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 3) {
                    maze[last.row][last.col].walls = 2;
                }
            }

            // Direction in: RIGHT
            else if (maze[current.row][current.col].directionIn == 3) {
                if (maze[last.row][last.col].neighbours == 0) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 1) {
                    maze[last.row][last.col].walls = 1;
                } else if (maze[last.row][last.col].neighbours == 2) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 3) {
                    maze[last.row][last.col].walls = 1;
                }
            }
        }

        // If any other cell in maze
        else {
            // Direction in: UP
            if (maze[current.row][current.col].directionIn == 0) {
                if (maze[last.row][last.col].neighbours == 0) {
                    maze[last.row][last.col].walls = 2;
                } else if (maze[last.row][last.col].neighbours == 1) {
                    maze[last.row][last.col].walls = 1;
                } else if (maze[last.row][last.col].neighbours == 2) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 3) {
                    maze[last.row][last.col].walls = 0;
                }
            }

            // Direction in: LEFT
            else if (maze[current.row][current.col].directionIn == 1) {
                if (maze[last.row][last.col].neighbours == 0) {
                    maze[last.row][last.col].walls = 2;
                } else if (maze[last.row][last.col].neighbours == 1) {
                    maze[last.row][last.col].walls = 1;
                } else if (maze[last.row][last.col].neighbours == 2) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 3) {
                    maze[last.row][last.col].walls = 0;
                }
            }

            // Direction in: DOWN
            else if (maze[current.row][current.col].directionIn == 2) {
                if (maze[last.row][last.col].neighbours == 0) {
                    maze[last.row][last.col].walls = 2;
                } else if (maze[last.row][last.col].neighbours == 1) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 2) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 3) {
                    maze[last.row][last.col].walls = 2;
                }
            }

            // Direction in: RIGHT
            else if (maze[current.row][current.col].directionIn == 3) {
                if (maze[last.row][last.col].neighbours == 0) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 1) {
                    maze[last.row][last.col].walls = 1;
                } else if (maze[last.row][last.col].neighbours == 2) {
                    maze[last.row][last.col].walls = 3;
                } else if (maze[last.row][last.col].neighbours == 3) {
                    maze[last.row][last.col].walls = 1;
                }
            }

        }

    }

    // Utility function to display maze
    private static void printMaze(MazeCell[][] maze) {
        StringBuilder str = new StringBuilder("");
        for(int i = 0; i < numOfRows; i++)
        {
            for(int j = 0; j < numOfCols; j++)
            {
                if(maze[i][j].walls == 0) {
                    str.append("__|");
                } else if(maze[i][j].walls == 1) {
                    str.append("__ ");
                } else if(maze[i][j].walls == 2) {
                    str.append("  |");
                } else if(maze[i][j].walls == 3) {
                    str.append("   ");
                }
            }
            System.out.println(str);
            str.setLength(0);
        }

        // for(int i = 0; i < numOfRows; i++) {
        //     for(int j = 0; j < numOfCols; j++) {
        //         System.out.print(maze[i][j].walls);
        //     }
        // }
    }

    // Finds last adjacent cell in maze
    private static Coord findLastAdjacent(Coord current, Coord last, ArrayList<Coord> visitOrderList) {
        // Check if last cell was adjacent
        if ((last.row == current.row - 1 && last.col == current.col) ||
                (last.row == current.row + 1 && last.col == current.col) ||
                (last.col == current.col - 1 && last.row == current.row) ||
                (last.col == current.col + 1 && last.row == current.row)) {
            return last;
        } else {
            // Find most recent adjacent cell and set as last
            for (int i = visitOrderList.size() - 1; i >= 0; i--) {
                if ((visitOrderList.get(i).row == current.row - 1 && visitOrderList.get(i).col == current.col) ||
                        (visitOrderList.get(i).row == current.row + 1 && visitOrderList.get(i).col == current.col) ||
                        (visitOrderList.get(i).col == current.col - 1 && visitOrderList.get(i).row == current.row) ||
                        (visitOrderList.get(i).col == current.col + 1 && visitOrderList.get(i).row == current.row)) {
                    lastNotAdjacent = true;
                    return visitOrderList.get(i);
                }
            }
        }
        return last;
    }
}


