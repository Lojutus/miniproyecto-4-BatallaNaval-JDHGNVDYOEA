package batallanaval;

import com.example.batallanaval.model.Classes.Boards.Board;
import com.example.batallanaval.model.Classes.Ships.Destroyer;
import com.example.batallanaval.model.Classes.Ships.Frigate;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.ShotResult;
import com.example.batallanaval.model.Exceptions.InvalidShotException;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;
import com.example.batallanaval.model.Exceptions.OverlappingShipException;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void newBoardShouldHaveAllCellsEmpty() {
        Assertions.assertFalse(board.wasAlreadyShot(new Coordinate(0, 0)));
    }

    @Test
    void placingShipWithinBoundsShouldSucceed() throws Exception {
        Frigate frigate = new Frigate(new Coordinate(5, 5), Orientation.HORINZONTAL);

        Assertions.assertDoesNotThrow(() -> board.placeShip(frigate));
    }

    @Test
    void placingShipOutOfBoundsShouldThrowException() {
        Frigate frigate = new Frigate(new Coordinate(9, 9), Orientation.HORINZONTAL);
        Destroyer destroyer = new Destroyer(new Coordinate(9, 9), Orientation.HORINZONTAL);

        Assertions.assertThrows(OutOfBoardException.class, () -> board.placeShip(destroyer));
    }

    @Test
    void placingOverlappingShipsShouldThrowException() throws Exception {
        Frigate first = new Frigate(new Coordinate(3, 3), Orientation.HORINZONTAL);
        board.placeShip(first);

        Frigate second = new Frigate(new Coordinate(3, 3), Orientation.VERTICAL);

        Assertions.assertThrows(OverlappingShipException.class, () -> board.placeShip(second));
    }

    @Test
    void shootingWaterCellShouldReturnWaterResult() throws Exception {
        ShotResult result = board.shoot(new Coordinate(1, 1));

        Assertions.assertEquals(ShotResult.WATER, result);
    }

    @Test
    void shootingShipCellShouldReturnHitOrSunkResult() throws Exception {
        Frigate frigate = new Frigate(new Coordinate(4, 4), Orientation.HORINZONTAL);
        board.placeShip(frigate);

        ShotResult result = board.shoot(new Coordinate(4, 4));

        Assertions.assertEquals(ShotResult.SUNK, result);
    }

    @Test
    void shootingSameCellTwiceShouldThrowException() throws Exception {
        board.shoot(new Coordinate(2, 2));

        Assertions.assertThrows(InvalidShotException.class, () -> board.shoot(new Coordinate(2, 2)));
    }

    @Test
    void allShipsSunkShouldBeFalseWhileShipsRemain() throws Exception {
        Frigate frigate = new Frigate(new Coordinate(6, 6), Orientation.HORINZONTAL);
        board.placeShip(frigate);

        Assertions.assertFalse(board.allShipsSunk());
    }

    @Test
    void allShipsSunkShouldBeTrueAfterAllShipsAreSunk() throws Exception {
        Frigate frigate = new Frigate(new Coordinate(6, 6), Orientation.HORINZONTAL);
        board.placeShip(frigate);
        board.shoot(new Coordinate(6, 6));

        Assertions.assertTrue(board.allShipsSunk());
    }
}