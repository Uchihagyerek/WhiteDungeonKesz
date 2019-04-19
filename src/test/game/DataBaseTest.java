package game;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataBaseTest {

    @Test
    public void testScores(){

        DataBase.newPlayer ("Test");
        DataBase.setScore (99999,"Test");
        List<String[]> scores = DataBase.getScores ();
        assertEquals(scores.get (0)[1],"99999");
        DataBase.deleteScore (99999,"Test");
    }
}
