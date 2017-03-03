package test.com.sparkcognition;

import com.sparkcognition.Maze;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by davidauld on 2017-03-02.
 */
public class MazeTest {
    Maze testMaze;
    Maze testMaze2;
    @org.junit.Before
    public void setUp() throws Exception {
        testMaze = new Maze("(3,3)-[34,14,12,6,77,5,1,19,9]",3);
        testMaze2 = new Maze("(3,3)-[34,14,12,6,77,5,1,19,9]",1);
    }

    @org.junit.Test
    public void getDirections() throws Exception {
        List<String> mazeDirections1 = new ArrayList<>();
        mazeDirections1.add("'up'");
        mazeDirections1.add("'up'");
        mazeDirections1.add("'left'");
        Assert.assertEquals(mazeDirections1, testMaze.getDirections());

        List<String> mazeDirections2 = new ArrayList<>();
        mazeDirections2.add("'right'");
        mazeDirections2.add("'up'");
        mazeDirections2.add("'up'");
        mazeDirections2.add("'left'");
        mazeDirections2.add("'left'");
        Assert.assertEquals(mazeDirections2, testMaze2.getDirections());
    }

}