import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @org.junit.jupiter.api.Test
    void distBetween() {
        Board b = new Board(5, 6);
        assertEquals(1, b.distBetween(new Coord(1, 1), new Coord(2, 1))); //normal
        assertEquals(2, b.distBetween(new Coord(0, 2), new Coord(3, 4))); //wrap x-axis
        assertEquals(2, b.distBetween(new Coord(2, 0), new Coord(1, 5))); //wrap y-axis
        assertEquals(2, b.distBetween(new Coord(0, 0), new Coord(4, 5))); //wrap corner
    }

    @org.junit.jupiter.api.Test
    void coordFromMove() {
        Board b = new Board(5, 6);
        Coord start = new Coord(0,0);
        assertEquals(new Coord(0, 5), b.coordFromMove(start, Move.UP));
        assertEquals(new Coord(0, 1), b.coordFromMove(start, Move.DOWN));
        assertEquals(new Coord(4, 0), b.coordFromMove(start, Move.LEFT));
        assertEquals(new Coord(1, 0), b.coordFromMove(start, Move.RIGHT));
    }

    @org.junit.jupiter.api.Test
    void movesFrom() {
        Board b = new Board(5, 6);
        Coord start = new Coord(0,0);
        List<Tuple<Move, Coord>> expected = List.of(new Tuple<>(Move.UP, new Coord(0,5)),
                                                    new Tuple<>(Move.DOWN, new Coord(0, 1)),
                                                    new Tuple<>(Move.LEFT, new Coord(4, 0)),
                                                    new Tuple<>(Move.RIGHT, new Coord(1, 0)));

        List<Tuple<Move, Coord>> received = b.movesFrom(start);
        assertTrue(expected.size() == received.size() && expected.containsAll(received) && received.containsAll(expected));
    }

    @org.junit.jupiter.api.Test
    void safeMovesFrom() {
        GameSnake testSnake = new GameSnake("alive 5 0 4,5 4,5 4,0 4,0 0,0 0,2");
        Board b = new Board(5, 6, new Coord(1,1), testSnake, new GameSnake[0]);
        Coord start = new Coord(4, 1);
        List<Tuple<Move, Coord>> expected = List.of(
                new Tuple<>(Move.DOWN, new Coord(4, 2)),
                new Tuple<>(Move.LEFT, new Coord(3, 1)));
        List<Tuple<Move, Coord>> received = b.safeMovesFrom(start);
        assertTrue(expected.size() == received.size() && expected.containsAll(received) && received.containsAll(expected));
    }

    @org.junit.jupiter.api.Test
    void doubleSafeMovesFrom() {
        GameSnake testSnake = new GameSnake("alive 5 0 4,5 4,5 4,0 4,0 0,0 0,2");
        GameSnake testSnake2 = new GameSnake("alive 2 0 1,2 2,2");
        Board b = new Board(5, 6, new Coord(1,1), testSnake, new GameSnake[]{testSnake2});
        Coord start = new Coord(4, 1);
        List<Tuple<Move, Coord>> expected = List.of(
                new Tuple<>(Move.LEFT, new Coord(3, 1)));
        List<Tuple<Move, Coord>> received = b.doubleSafeMovesFrom(start);
        assertTrue(expected.size() == received.size() && expected.containsAll(received) && received.containsAll(expected));
    }

    @org.junit.jupiter.api.Test
    void reachableDoubleSafeFrom() {
        GameSnake testSnake = new GameSnake("alive 5 0 4,5 4,5 4,0 4,0 0,0 0,2");
        GameSnake testSnake2 = new GameSnake("alive 2 0 2,1 2,2");
        Board b = new Board(5, 6, new Coord(1,1), testSnake, new GameSnake[]{testSnake2});
        Coord start = new Coord(4, 4);
        assertEquals(10, b.reachableDoubleSafeFrom(start, 1000).size());
    }
}