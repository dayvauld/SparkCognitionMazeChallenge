# Maze Challenge

Simple program to input a text-based maze and have the solved directions output to terminal.

Usage
------
*java com.sparkcognition.Main <\input file.txt>*

The input text file must follow this format:<br>
(\<rows>,\<columns>)-(#,#,#,#,...)<br>
(\<rows>,\<columns>)-(#,#,#,#,...)<br>
(\<rows>,\<columns>)-(#,#,#,#,...)<br>
...

To Compile (From cloned project):<br>
*javac /src/com/sparkcognition/Main.java /src/com/sparkcognition/Maze.java*

May change based on where you may be located in terminal, navigate to 'SparkCognitionMazeChallenge/src/' directory and run:<br>
*java com.sparkcognition.Main ../mazes.txt*

*Built in IntellJ IDEA (MAC OS X) using JDK8, can also import project into IDE and run program from there*

## Requirements

- User is able to input text file with expected
- User is able to read outputted solved maze directions from terminal/console

## Maze Solving Algorithm

Based on how the maze can be represented as a graph-type data structure we can perform any type of graph searching algorithm to find a solution. This program uses a BFS (Breadth First Search) to find a solution. The BFS is an optimal algorithm that is guaranteed to find the best solution that exists. The BFS algorithm is straighforward to implement which is why I chose it. In this challenge it is implemented with a simple Java framework Queue.

The standard algorithm is altered to work with the type of maze(matrix) encoding used in the text file. Additionally it is modified to support the addition of 'Mines' within the maze. If any of the algorithm solving branches encounters a mine, that branch is deducted a life. By default the number is lives is set to (3). If the branch loses all lives then that branch is considered invalid and will be removed as a possible solution. It is possible that there will be no solutions if there is not enough lives and in that case there will be no valid path from the start to the end.


## Debugging / Auxiliary Methods

A number of auxiliary debug methods were added in development to help visualize and verify that the algorithm worked as expected. These can be enabled by setting the boolean debug argument to 'true' within the Maze.java class. This will output the raw number matrix as well as a visual ASCII representation of the input matrices.

