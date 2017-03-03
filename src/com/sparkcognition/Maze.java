package com.sparkcognition;

import java.util.*;

/**
 * Created by davidauld on 2017-03-01.
 */

public class Maze {
    //DEBUG - Set to true to output verbose debugging messages and drawn mazes
    private boolean debug = false;

    final static private int UP_MASK = 1;
    final static private int RIGHT_MASK = 2;
    final static private int DOWN_MASK = 4;
    final static private int LEFT_MASK = 8;
    final static private int START_MASK = 16;
    final static private int END_MASK = 32;
    final static private int MINE_MASK = 64;

    private String rawMazeString;
    private String rawNumberMatrixString;
    private int rows = 0;
    private int columns = 0;
    private int [][] mazeMatrix;
    private int [] start = new int[2];
    private int [] end = new int[2];
    private int numOfLives = 0;

    /**
     * Constructs a new Maze by taking an input Maze String and converting it into a Maze object.
     */
    public Maze(String rawString, int numOfLives) {
        this.rawMazeString = rawString;
        this.numOfLives = numOfLives;

        if (numOfLives <= 0) throw new IllegalArgumentException("numOfLives must be above 0");

        //extract row and columns
        try {
            this.rows = Integer.parseInt(rawString.substring(rawString.indexOf('(') + 1, rawString.indexOf(',')));
            this.columns = Integer.parseInt(rawString.substring(rawString.indexOf(',') + 1, rawString.indexOf(')')));
            this.rawNumberMatrixString = rawString.substring(rawString.indexOf('-') + 2);
            this.rawNumberMatrixString = rawNumberMatrixString.substring(0, rawNumberMatrixString.length() - 1);
            //Build maze
            this.mazeMatrix = buildMaze(
                    rows,
                    columns,
                    getNumberArrayFromNumberString(rawNumberMatrixString));
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("There is an issue reading the input string. Please check the file is properly formatted.");
        }

        if(debug) {
            printRawMatrix(mazeMatrix);
            Point[][] maze = convertToPointMatrix(mazeMatrix);
            printMaze(convertToCharMatrix(maze));
            List<Point> path = solveMaze(maze, start[0], start[1]);
            printDirections(path);
        }
    }

    /**
     * Get String list of 'up,down,left,right' directions of a guaranteed best maze solution
     */
    public List<String> getDirections() {
        Point[][] maze = convertToPointMatrix(getIntMatrix());
        List<Point> path = solveMaze(maze, start[0], start[1]);

        List<String> directionPath = new ArrayList<>(path.size());
        for(int i = path.size() - 1;i > 0; i--) {
            //DOWN
            if (path.get(i).getX() < path.get(i - 1).getX()) {
                directionPath.add("'down'");
            }
            //LEFT
            if (path.get(i).getY() > path.get(i - 1).getY()) {
                directionPath.add("'left'");
            }
            //UP
            if (path.get(i).getX() > path.get(i - 1).getX()) {
                directionPath.add("'up'");
            }
            //RIGHT
            if (path.get(i).getY() < path.get(i - 1).getY()) {
                directionPath.add("'right'");
            }
        }
        return directionPath;
    }

    /**
     * Returns start cell of Matrix
     */
    private int[] getStartPoint() {
        return start;
    }

    /**
     * Returns matrix representation of maze
     */
    private int[][] getIntMatrix() {
        return mazeMatrix;
    }

    /**
     * Converts list of integers into a primitive int matrix with a specified number of rows and columns
     */
    private int[][] buildMaze(int rows, int columns, List<Integer> mazeArray) {
        int [][] matrix = new int[rows][columns];

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                matrix[i][j] = mazeArray.get(j + (i * columns));
            }
        }
        return matrix;
    }

    /**
     * Converts a trimmed string of just CSV int values into a List<Integer>
     */
    private List<Integer> getNumberArrayFromNumberString(String numbersArray) {
        List<String> numbers = Arrays.asList(numbersArray.split(","));

        List<Integer> numbersInt = new ArrayList<>();
        for (String number : numbers) {
            numbersInt.add(Integer.valueOf(number));
        }
        return numbersInt;
    }


    /**
     * Prints a list of human readable directions with a solved maze path.
     * Iterates backwards through list and compares the index values to determine
     * the direction.
     */
    private void printDirections(List<Point> path) {
        System.out.print("{");
        for(int i = path.size() - 1;i > 0; i--) {
            //UP
            if (path.get(i).getX() < path.get(i - 1).getX()) {
                System.out.print("'down'");
            }
            //RIGHT
            if (path.get(i).getY() > path.get(i - 1).getY()) {
                System.out.print("'left'");
            }
            //DOWN
            if (path.get(i).getX() > path.get(i - 1).getX()) {
                System.out.print("'up'");
            }
            //LEFT
            if (path.get(i).getY() < path.get(i - 1).getY()) {
                System.out.print("'right'");
            }
            if(i > 2) {
                System.out.print(",");
            }
        }
        System.out.println("}");
    }

    /**
     * Constructs a new Matrix with Points by converting the raw integer values to
     * a more understandable object
     */
    private Point[][] convertToPointMatrix(int [][] maze) {
        Point[][] pointMatrix = new Point[rows][columns];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                pointMatrix[i][j] = new Point(maze[i][j], i, j);
                if(pointMatrix[i][j].start) {
                    start = new int[]{i, j};
                } else if(pointMatrix[i][j].end) {
                    end = new int[]{i, j};
                }
            }
        }
        return pointMatrix;
    }

    /**
     * Solves maze using BFS algorithm and returns a list of points that is a solved path from the end to the beginning
     *
     */
    private List<Point> solveMaze(Point[][] maze, int startX, int startY) {
        Queue<Point> queue = new LinkedList<>();
        List<Point> path = new ArrayList<>();
        Point current = maze[startX][startY];
        queue.add(current); // Add start point to queue

        while(queue.peek() != null) {
            current = queue.remove();

            if(current.isMine()) {
                //Hit mine, minus life
                current.loseLife();
            }
            //If all lives are taken then this path is invalid
            if(current.getCurrentLives() > 0) {
                if (current.isEnd()) {
                    //Found maze end, traverse back through parents to obtain path to start
                    path.add(current);
                    while (current != null) {
                        path.add(current);
                        current = current.getParent();
                    }
                    return path;
                } else if (!current.isVisited()) {//Enqueue all possible neighbour paths && ensure they have not been visited
                    current.setVisited();
                    // RIGHT (Is right open && the right cell not already visited?)
                    if (current.isRightOpen() && !maze[current.getX()][current.getY() + 1].isVisited()) {
                        maze[current.getX()][current.getY() + 1].setParent(current); //Set parent of right cell as current cell
                        queue.add(maze[current.getX()][current.getY() + 1]); //Add to queue
                    }
                    // UP
                    if (current.isUpOpen() && !maze[current.getX() - 1][current.getY()].isVisited()) {
                        maze[current.getX() - 1][current.getY()].setParent(current);
                        queue.add(maze[current.getX() - 1][current.getY()]);
                    }
                    // LEFT
                    if (current.isLeftOpen() && !maze[current.getX()][current.getY() - 1].isVisited()) {
                        maze[current.getX()][current.getY() - 1].setParent(current);
                        queue.add(maze[current.getX()][current.getY() - 1]);
                    }
                    // DOWN
                    if (current.isDownOpen() && !maze[current.getX() + 1][current.getY()].isVisited()) {
                        maze[current.getX() + 1][current.getY()].setParent(current);
                        queue.add(maze[current.getX() + 1][current.getY()]);
                    }
                }
            }
        }
        return path;
    }

    /**
     * Constructs a new String by converting the specified array of
     * bytes using the platform's default character encoding.
     */
    private void printRawMatrix(int [][] maze) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(maze[i][j] < 10) {
                    System.out.print(maze[i][j] + "  ");
                } else {
                    System.out.print(maze[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Constructs a new String by converting the specified array of
     * bytes using the platform's default character encoding.
     */
    private char[][] convertToCharMatrix(Point[][] maze) {
        char[][] boolMatrix = new char[(rows * 2) + 1][(columns * 2) + 1];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if (maze[i][j].start) {
                    boolMatrix[(i * 2) + 1][(j * 2) + 1] = 'S';
                } else if (maze[i][j].end) {
                    boolMatrix[(i * 2) + 1][(j * 2) + 1] = 'E';
                } else if (!maze[i][j].mine) {
                    boolMatrix[(i * 2) + 1][(j * 2) + 1] = 'O';
                } else {
                    boolMatrix[(i * 2) + 1][(j * 2) + 1] = 'M';
                }
                if(maze[i][j].left) {
                    boolMatrix[(i * 2) + 1][(j * 2)] = 'O';
                }
                if(maze[i][j].right) {
                    boolMatrix[(i * 2) + 1][(j * 2) + 2] = 'O';
                }
                if(maze[i][j].up && j > 0) {
                    boolMatrix[i * 2][(j * 2) + 1] = 'O';
                }
                if(maze[i][j].down) {
                    boolMatrix[(i * 2) + 2][(j * 2) + 1] = 'O';
                }
            }
        }
        return boolMatrix;
    }

    /**
     * Constructs a new String by converting the specified array of
     * bytes using the platform's default character encoding.
     */
    private void printMaze(char [][] maze) {
        for(int i = 0; i < (rows * 2) + 1; i++) {
            for(int j = 0; j < (columns * 2) + 1; j++) {
                if(maze[i][j] == 'O') {
                    System.out.print("  ");
                } else if (maze[i][j] == 'M') {
                    System.out.print("M ");
                } else if (maze[i][j] == 'S') {
                    System.out.print("S ");
                } else if (maze[i][j] == 'E') {
                    System.out.print("E ");
                } else {
                    System.out.print("+ ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Point class converts and represents maze integer values into series of boolean values
     */
    private class Point {
        private int x;
        private int y;
        private boolean up = false;
        private boolean left = false;
        private boolean down = false;
        private boolean right = false;
        private boolean start = false;
        private boolean end = false;
        private boolean mine = false;
        private boolean visited = false;
        private Point parent = null;
        private int currentLives;

        Point(int value, int x, int y) {
            this.x = x;
            this.y = y;
            this.currentLives = numOfLives;

            if((value & UP_MASK) == UP_MASK)
                this.up = true;
            if((value & RIGHT_MASK) == RIGHT_MASK)
                this.right = true;
            if((value & DOWN_MASK) == DOWN_MASK)
                this.down = true;
            if((value & LEFT_MASK) == LEFT_MASK)
                this.left = true;
            if((value & START_MASK) == START_MASK)
                this.start = true;
            if((value & END_MASK) == END_MASK)
                this.end = true;
            if((value & MINE_MASK) == MINE_MASK)
                this.mine = true;
        }

        private int getX() {
            return this.x;
        }

        private int getY() {
            return this.y;
        }

        private boolean isRightOpen() { return this.right; }

        private boolean isUpOpen() { return this.up; }

        private boolean isDownOpen() { return this.down; }

        private boolean isLeftOpen() { return this.left; }

        private void setVisited() {
            this.visited = true;
        }

        private boolean isVisited() {
            return this.visited;
        }

        private void setParent(Point parent) {
            this.parent = parent;
        }

        private Point getParent() {
            return this.parent;
        }

        private boolean isMine() {
            return this.mine;
        }

        private boolean isStart() {
            return this.start;
        }

        private boolean isEnd() {
            return this.end;
        }

        private int getCurrentLives() {
            return this.currentLives;
        }

        private void loseLife() {
            this.currentLives--;
        }
    }
}
