import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class Strategies {
    public static Move adaptive(Board board, Coord headPos, Coord applePos, GameSnake[] otherSnakes, GameSnake us) {
        //check if there is anything safe
        if (board.safeMovesFrom(headPos).isEmpty()) {
            System.out.printf("log %d: no safe moves, going straight\n", Snake.turnNumber);
            return Move.STRAIGHT;
        }
        //check if we are longest
        int longestSnake = 0;
        for (GameSnake s: otherSnakes) {
            if (s.length > longestSnake) longestSnake = s.length;
        }
        //if (us.length > longestSnake*1.1) return claustrophobic(board, headPos);

        //Check if we can get a kill
        List<Tuple<Move, Coord>> possibleMoves = board.movesFrom(headPos);
        for (GameSnake s: otherSnakes) {
            if (s.length < us.length) {
                for (Tuple<Move, Coord> x: possibleMoves) {
                    if (s.getHeadPos().equals(x.y())) return x.x();
                }
            }
        }

        Move aStarCandidate = aStar(board, headPos, applePos, otherSnakes, us);
        if (aStarCandidate == null) {
            //apple isn't reachable, fallback to naive to make sure we dont kill ourselves
            System.out.printf("log %d: apple unreachable, falling back to claustrophobic\n", Snake.turnNumber);
            return claustrophobic(board, headPos);
        } else {
            return aStarCandidate;
        }
    }

    public static Move aStar(Board board, Coord headPos, Coord applePos, GameSnake[] otherSnakes, GameSnake us) {
        //working queue
        PriorityQueue<AstarEntry> pq = new PriorityQueue<>();

        //finalised nodes
        HashSet<AstarEntry> finalised = new HashSet<>();

        //add source vertex
        AstarEntry source = new AstarEntry(headPos, null, 0, board.distBetween(headPos, applePos), null);
        pq.add(source);

        AstarEntry result = null;
        //main loop
        while (!pq.isEmpty()) {
            //finalise the top of the PQ
            AstarEntry head = pq.remove();
            //check if we have hit the apple
            if (head.location().equals(applePos)) {
                result = head;
                break;
            }
            //check if already finalised
            if (finalised.contains(head)) continue;

            //add to finalised array
            finalised.add(head);

            ArrayList<Tuple<Move, Coord>> adjacencies = board.safeMovesFrom(head.location());
            for (Tuple<Move, Coord> x : adjacencies) {
                Coord c = x.y();
                int risk = calculateRisk(c, otherSnakes, board, us.length);
                int dist = board.distBetween(c, applePos);
                AstarEntry newEntry = new AstarEntry(c, head, head.cumulativeCost() + risk, dist, x.x());
                pq.add(newEntry);
                if (newEntry.location().equals(applePos)) result = newEntry;
            }
        }

        //now trace path back from apple
        if (result == null) {
            return null;
        }
        AstarEntry curr = result;
        while (!curr.prev().equals(source)) {
            curr = curr.prev();
        }
        //now curr is the first node in the chain
        return curr.direction();

    }

    static int calculateRisk(Coord c, GameSnake[] otherSnakes, Board board, int ourLength) {
        //find distance to closest snake within 10 of us
        int closestSnakeDist = Integer.MAX_VALUE;
        for (GameSnake s: otherSnakes) {
            Coord otherHeadPos = s.getHeadPos();
            int dist = board.distBetween(c, otherHeadPos);
            if (dist < closestSnakeDist) closestSnakeDist = dist;

        }

        int risk = ourLength - closestSnakeDist*closestSnakeDist;
        if (risk < 1) risk = 1;
        return risk;
    }

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

    public static Move claustrophobic(Board board, Coord c) {
        ArrayList<Tuple<Move, Coord>> safeMoves = board.safeMovesFrom(c);
        if (safeMoves.isEmpty()) return Move.STRAIGHT;
        int bestSpace = 0;
        Move bestMove = Move.STRAIGHT;
        for (Tuple<Move, Coord> x: safeMoves) {
            int currSpace = board.reachableDoubleSafeFrom(x.y(), 500).size();
            if (currSpace > bestSpace) {
                bestMove = x.x();
            }
        }
        return bestMove;
    }
}
