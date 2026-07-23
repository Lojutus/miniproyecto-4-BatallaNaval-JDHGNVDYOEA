package batallanaval;

import com.example.batallanaval.model.Classes.Ships.Destroyer;
import com.example.batallanaval.model.Classes.Ships.Frigate;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import com.example.batallanaval.model.Classes.Utils.ShotResult;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ShipTest {

    @Test
    void frigateShouldOccupyExactlyOneCell() {
        Frigate frigate = new Frigate(new Coordinate(3, 4), Orientation.HORINZONTAL);

        Assertions.assertEquals(1, frigate.getOccupiedCells().size());
        Assertions.assertTrue(frigate.getOccupiedCells().contains(new Coordinate(3, 4)));
    }

    @Test
    void destroyerHorizontalShouldOccupyConsecutiveColumns() {
        Destroyer destroyer = new Destroyer(new Coordinate(2, 2), Orientation.HORINZONTAL);

        Assertions.assertEquals(2, destroyer.getOccupiedCells().size());
        Assertions.assertTrue(destroyer.getOccupiedCells().contains(new Coordinate(2, 2)));
        Assertions.assertTrue(destroyer.getOccupiedCells().contains(new Coordinate(2, 3)));
    }

    @Test
    void destroyerVerticalShouldOccupyConsecutiveRows() {
        Destroyer destroyer = new Destroyer(new Coordinate(2, 2), Orientation.VERTICAL);

        Assertions.assertTrue(destroyer.getOccupiedCells().contains(new Coordinate(2, 2)));
        Assertions.assertTrue(destroyer.getOccupiedCells().contains(new Coordinate(3, 2)));
    }

    @Test
    void shipShouldNotBeSunkWithoutHits() {
        Frigate frigate = new Frigate(new Coordinate(0, 0), Orientation.HORINZONTAL);

        Assertions.assertFalse(frigate.isSunk());
    }

    @Test
    void frigateShouldBeSunkAfterSingleHit() throws Exception {
        Frigate frigate = new Frigate(new Coordinate(0, 0), Orientation.HORINZONTAL);

        ShotResult result = frigate.shoot(new Coordinate(0, 0));

        Assertions.assertEquals(ShotResult.SUNK, result);
        Assertions.assertTrue(frigate.isSunk());
    }

    @Test
    void destroyerShouldBeHitButNotSunkAfterOneOfTwoHits() throws Exception {
        Destroyer destroyer = new Destroyer(new Coordinate(0, 0), Orientation.HORINZONTAL);

        ShotResult result = destroyer.shoot(new Coordinate(0, 0));

        Assertions.assertEquals(ShotResult.HIT, result);
        Assertions.assertFalse(destroyer.isSunk());
    }

    @Test
    void shootingCoordinateNotBelongingToShipShouldThrowException() {
        Frigate frigate = new Frigate(new Coordinate(0, 0), Orientation.HORINZONTAL);

        Assertions.assertThrows(OutOfBoardException.class, () -> frigate.shoot(new Coordinate(5, 5)));
    }
}
