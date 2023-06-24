/*
 * Name			: 		Dillon Latimore
 * File 		: 		MazeSolverDFS.java
 */

import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class MazeSolverDFS {
    
    // Start and finish coordinates
    private static Coord startCoord;
    private static Coord finishCoord;

    // Initialize direction vectors
    private static int dRow[] = {-1, 0, 0, 1};
    private static int dCol[] = {0, -1, 1, 0};

    // Keeps info about each maze coordinate and its corresponding cell number
    private static class Coord
    {
        private int number;
        private int row;
        private int col;

        private Coord(int number, int row, int col)
        {
            this.number = number;
            this.row = row;
            this.col = col;
        }
    }

    // Main method
    public static void main(String[] args) {
        // Record start time of program
        long startTime = System.currentTimeMillis();
        // List of wall values for maze
        ArrayList<Integer> wallsList = new ArrayList<Integer>();
        // Sets as user input 
        String fileName = args[0];
        // Creates a string from the file
        String inputString = readFile(fileName);
        // Stores the start and finish cells and number of rows and columns
        int[] mazeVals = getMazeValues(inputString, wallsList);
        // Creates 2d array of cells and sets each with a number starting from 1
        int grid[][] = new int[mazeVals[0]][mazeVals[1]];
        int val = 1;
        for(int i = 0; i < mazeVals[0]; i++)
        {
            for(int j = 0; j < mazeVals[1]; j++)
            {
                grid[i][j] = val;
                val++;
            }
        }

        // Creates 2d array for tracking what cells are visited
        Boolean vis[][] = new Boolean[mazeVals[0]][mazeVals[1]];
        for(int i = 0; i < mazeVals[0]; i++)
        {
            for(int j = 0; j < mazeVals[1]; j++)
            {
                vis[i][j] = false;
            }
        }

        // Creates maze with walls
        val = 0;
        int maze[][] = new int[mazeVals[0]][mazeVals[1]];
        for(int i = 0; i < mazeVals[0]; i++)
        {
            for(int j = 0; j < mazeVals[1]; j++)
            {
                maze[i][j] = wallsList.get(val);
                val++;
            }
        }

        // Sets start and finish coordinates
        for(int i = 0; i < mazeVals[0]; i++)
        {
            for(int j = 0; j < mazeVals[1]; j++)
            {
                if(grid[i][j] == mazeVals[2]) {
                    startCoord = new Coord(grid[i][j], i, j);
                }

                if(grid[i][j] == mazeVals[3]) {
                    finishCoord = new Coord(grid[i][j], i, j);
                }
            }
        }

        // Utility function to display maze
        printMaze(maze, mazeVals);

        // Performs depth first search to solve maze
        DFS(startCoord.row, startCoord.col, finishCoord.row, finishCoord.col, grid, vis, maze, mazeVals);
        // Records time taken to run program
        long timeElapsed = System.currentTimeMillis()-startTime;
        // Output program run time
        System.out.println(timeElapsed);
    }

    // Gets the maze values from the input file string
    private static int[] getMazeValues(String input, ArrayList<Integer> wallsList) {
        int rows;
        int cols;
        int start;
        int finish;

        char someChar = ',';
        int count = 0;
        String substr = "";

        while(input.charAt(count) != someChar) {
            substr += input.charAt(count);
            count++;
        }

        rows = Integer.parseInt(substr);

        count++;
        someChar = ':';
        substr = "";

        while(input.charAt(count) != someChar) {
            substr += input.charAt(count);
            count++;
        }

        cols = Integer.parseInt(substr);
        
        count++;
        someChar = ':';
        substr = "";

        while(input.charAt(count) != someChar) {
            substr += input.charAt(count);
            count++;
        }

        start = Integer.parseInt(substr);

        count++;
        someChar = ':';
        substr = "";

        while(input.charAt(count) != someChar) {
            substr += input.charAt(count);
            count++;
        }

        finish = Integer.parseInt(substr);

        count++;

        for (int i = count; i < input.length(); i++) {
            wallsList.add(Character.getNumericValue(input.charAt(i)));
        }

        // for (int i = 0; i < wallsList.size(); i++) {
        //     System.out.println(wallsList.get(i));
        // }

        int[] mazeVals = {rows, cols, start, finish}; 
        return mazeVals;
    }

    // Returns a string from the input file
    private static String readFile(String fileName) {
        String data = "";
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              data = myReader.nextLine();
            }
            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }    
        return data;
    }

    // Checks if coordinate is the finish coordinate
    private static Boolean isFinish(int row, int col, int finishX, int finishY)
    {
        if(row == finishX && col == finishY) {
            return true;
        } else {
            return false;
        }      
    }

    // Utility function to display maze
    private static void printMaze(int[][] maze, int[] mazeVals) {
        StringBuilder str = new StringBuilder("");
        for(int i = 0; i < mazeVals[0]; i++)
        {
            for(int j = 0; j < mazeVals[1]; j++)
            {
                if(maze[i][j] == 0) {
                    str.append("__|");
                } else if(maze[i][j] == 1) {
                    str.append("__ ");
                } else if(maze[i][j] == 2) {
                    str.append("  |");
                } else if(maze[i][j] == 3) {
                    str.append("   ");
                }
            }
            System.out.println(str);
            str.setLength(0);
        }

        // for(int i = 0; i < mazeVals[0]; i++) {
        //     for(int j = 0; j < mazeVals[1]; j++) {
        //         System.out.print(maze[i][j]);
        //     }
        // }
        System.out.println("");
    }

    // Performs depth first search of maze to solve it
    private static void DFS(int startX, int startY, int finishX, int finishY,  int grid[][],
                    Boolean vis[][], int maze[][], int[] mazeVals)
    {
        // Setup list of visited cells
        ArrayList<Integer> visitedCells = new ArrayList<Integer>();
       // Setup stack
        Stack<Coord> st = new Stack<Coord>();
        // Push start coord to stack
        st.push(new Coord(grid[startX][startY], startX, startY));
        // Set current as start coord
        Coord current = st.peek();
        
        // Iterate until the stack is not empty
        while (!st.empty())
        {
            // Set current as top value on stack
            current = st.peek();

            // Set current as visited
            vis[current.row][current.col] = true;

            // Add current coords cell number to visited cells list
            visitedCells.add(current.number);
            
            // If at finish coording then break from loop
            if(isFinish(current.row, current.col, finishX, finishY)) {  
                break;
            }

            // Checks for adjacent cells that can be visited
            boolean cellFound = false;
            for(int i = 0; i < 4; i++)
            {
                int adjx = current.row + dRow[i];
                int adjy = current.col + dCol[i];
                // If cell is in the bounds of the maze
                if (adjx >= 0 && adjy >= 0 && adjx < mazeVals[0] && adjy < mazeVals[1]) {
                    // If cell hasn't already been visited
                    if(!vis[adjx][adjy]) {
                        
                        // UP
                        if(i==0) {
                            if(maze[adjx][adjy] != 0 && maze[adjx][adjy] != 1) {
                                st.push(new Coord(grid[adjx][adjy], adjx, adjy));
                                cellFound = true;
                                break;
                            }
                        }

                        // LEFT
                        if(i==1) {
                            if(maze[adjx][adjy] != 0 && maze[adjx][adjy] != 2) {
                                st.push(new Coord(grid[adjx][adjy], adjx, adjy));
                                cellFound = true;
                                break;
                            }
                        }

                        // RIGHT
                        if(i==2) {
                            if(maze[current.row][current.col] != 0 && maze[current.row][current.col] != 2) {
                                st.push(new Coord(grid[adjx][adjy], adjx, adjy));
                                cellFound = true;
                                break;
                            }
                        }

                        // DOWN
                        if(i==3) {
                            if(maze[current.row][current.col] != 0 && maze[current.row][current.col] != 1) {
                                st.push(new Coord(grid[adjx][adjy], adjx, adjy));
                                cellFound = true;
                                break;
                            }
                        }
                    }
                }
            }

            // If no adjacent cell found pop from stack
            if(!cellFound) {
                st.pop();
            }
            
        }

        // Prints the order the cells were traversed and how many steps were taken
        System.out.print("(");
        for(int i=0; i<visitedCells.size(); i++) {
            System.out.print(visitedCells.get(i) + ",");
        }
        System.out.print("\b");
        System.out.print(")");
        System.out.println("");
        System.out.println(visitedCells.size()-1);
    }
}
