package com.sparkcognition;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int numOfLives = 3;

	    //File reader
        List<String> mazesStrings = new ArrayList<>();
        try {
            mazesStrings = Files.readAllLines(Paths.get(args[0]), Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println("Error reading input .txt file. Ensure file follows expected format.");
            System.out.println("(<rows>,<columns>)-(#,#,#,...)");
            e.printStackTrace();
        }

        // Maze Builder
        for(String mazeStr : mazesStrings) {
            Maze maze = new Maze(mazeStr, numOfLives);
            List<String> directions = maze.getDirections();
            printDirections(directions);
        }
    }

    private static void printDirections(List<String> directions) {
        System.out.print("[");
        for(int i = 0; i < directions.size(); i++) {
            System.out.print(directions.get(i));
            //Add ',' between directions
            if(i < directions.size() - 1)
                System.out.print(",");
        }
        System.out.println("]");
    }
}
