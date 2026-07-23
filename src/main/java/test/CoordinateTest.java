package test;

import com.example.batallanaval.model.Classes.Utils.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CoordinateTest {

    @Test
    void twoCoordinatesWithSameValuesShouldBeEqual() {
        Coordinate a = new Coordinate(3, 4);
        Coordinate b = new Coordinate(3, 4);

        Assertions.assertEquals(a, b);
    }

    @Test
    void twoCoordinatesWithDifferentValuesShouldNotBeEqual() {
        Coordinate a = new Coordinate(3, 4);
        Coordinate b = new Coordinate(5, 6);

        Assertions.assertNotEquals(a, b);
    }

    @Test
    void equalCoordinatesShouldHaveSameHashCode() {
        Coordinate a = new Coordinate(3, 4);
        Coordinate b = new Coordinate(3, 4);

        Assertions.assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void coordinateWithinBoundsShouldReturnTrue() {
        Coordinate c = new Coordinate(5, 5);

        Assertions.assertTrue(c.isOutOfBoard(10, 10));
    }

    @Test
    void coordinateOutOfBoundsShouldReturnFalse() {
        Coordinate c = new Coordinate(10, 3);

        Assertions.assertFalse(c.isOutOfBoard(10, 10));
    }

    @Test
    void setUsedAsHashSetShouldNotAllowLogicalDuplicates() {
        java.util.Set<Coordinate> set = new java.util.HashSet<>();
        set.add(new Coordinate(2, 2));
        set.add(new Coordinate(2, 2)); // mismo valor, objeto distinto

        Assertions.assertEquals(1, set.size());
    }
}