import java.util.ArrayList;

public class Strategies {
    public static Move naive(Board board, Coord headPos, Coord applePos) {

        ArrayList<Tuple<Move, Coord>> safeMoves = board.safeMovesFrom(headPos);
        if (safeMoves.isEmpty()) return Move.STRAIGHT; //we are cooked

        //Find the best move
        int bestDist = Integer.MAX_VALUE;
        Move bestMove = Move.STRAIGHT;

        for (Tuple<Move, Coord> m: safeMoves) {
            int dist = board.distBetween(applePos, m.y());
            if (dist < bestDist) {
                bestDist = dist;
                bestMove = m.x();
            }
        }

        return bestMove;
    }
}
