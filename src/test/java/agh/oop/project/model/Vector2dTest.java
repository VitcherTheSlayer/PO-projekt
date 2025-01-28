package agh.oop.project.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Vector;

public class Vector2dTest {
    @Test
    public void equalsTest(){
        var _2_4 = new Vector2d(2, 4);
        var _1plus1_6minus2 = new Vector2d(1 + 1, 6 - 2);
        var otherTypeVector = new Vector<Integer>();

        Assertions.assertEquals(_2_4, _1plus1_6minus2);
        Assertions.assertNotEquals(_2_4, null);
        Assertions.assertNotEquals(_2_4, otherTypeVector);
        Assertions.assertNotEquals(_1plus1_6minus2, null);
        Assertions.assertNotEquals(_1plus1_6minus2, otherTypeVector);
    }
    @Test
    public void toStringTest(){
        var _420_1337 = new Vector2d(420, 1337);

        Assertions.assertEquals(_420_1337.toString(), "(420, 1337)");
    }
    @Test
    public void precedesTest(){
        var _1_1 = new Vector2d(1, 1);
        var _2_4 = new Vector2d(2, 4);
        var minus1_3 = new Vector2d(-1, 3);

        Assertions.assertTrue(_1_1.precedes(_2_4));
        Assertions.assertFalse(_1_1.precedes(minus1_3));
        Assertions.assertTrue(minus1_3.precedes(_2_4));
    }
    @Test
    public void followsTest(){
        var _1_1 = new Vector2d(1, 1);
        var _2_4 = new Vector2d(2, 4);
        var minus1_3 = new Vector2d(-1, 3);

        Assertions.assertTrue(_2_4.follows(_1_1));
        Assertions.assertFalse(minus1_3.follows(_1_1));
        Assertions.assertTrue(_2_4.follows(minus1_3));
    }

    @Test
    public void upperRightTest(){
        var _2_1 = new Vector2d(2, 1);
        var _1_2 = new Vector2d(1, 2);

        var _2_2 = new Vector2d(2, 2);

        Assertions.assertEquals(_2_1.upperRight(_1_2), _2_2);
        Assertions.assertEquals(_1_2.upperRight(_2_1), _2_2);
    }
    @Test
    public void lowerLeftTest(){
        var _2_1 = new Vector2d(2, 1);
        var _1_2 = new Vector2d(1, 2);

        var _1_1 = new Vector2d(1, 1);

        Assertions.assertEquals(_2_1.lowerLeft(_1_2), _1_1);
        Assertions.assertEquals(_1_2.lowerLeft(_2_1), _1_1);
    }
    @Test
    public void addTest(){
        var _2_1 = new Vector2d(2, 1);
        var _1_2 = new Vector2d(1, 2);

        var _3_3 = new Vector2d(3, 3);

        Assertions.assertEquals(_2_1.add(_1_2), _3_3);
    }
    @Test
    public void subtractTest(){
        var _2_1 = new Vector2d(2, 1);
        var _1_2 = new Vector2d(1, 2);

        var _1_minus1 = new Vector2d(1, -1);

        Assertions.assertEquals(_2_1.subtract(_1_2), _1_minus1);
    }
    @Test
    public void oppositeTest(){
        var _2_minus1 = new Vector2d(2, -1);

        var minus2_1 = new Vector2d(-2, 1);

        Assertions.assertEquals(_2_minus1.opposite(), minus2_1);
    }
}
