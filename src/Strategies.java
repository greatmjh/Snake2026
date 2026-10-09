import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Strategies {
    static int turnNumber = 0;
    public static Move aStar(Board board, Coord headPos, Coord applePos, GameSnake[] otherSnakes) {
        turnNumber++;
        //working queue
        PriorityQueue<AstarEntry> pq = new PriorityQueue<>();

        //finalised nodes
        HashSet<AstarEntry> finalised = new HashSet<>();

        //add source vertex
        AstarEntry source = new AstarEntry(headPos, null, 0, board.distBetween(headPos, applePos), null);
        pq.add(source);

        AstarEntry result = source;
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
                int risk = calculateRisk(c, otherSnakes, board);
                int dist = board.distBetween(c, applePos)*0;
                AstarEntry newEntry = new AstarEntry(c, head, head.cumulativeCost() + risk, dist, x.x());
                pq.add(newEntry);
                if (newEntry.location().equals(applePos)) result = newEntry;
            }
        }

        //now trace path back from apple

        try {
            AstarEntry curr = result;
            while (!curr.prev().equals(source)) {
                curr = curr.prev();
            }
            //now curr is the first node in the chain
            return curr.direction();
        } catch (NullPointerException e) {
            System.err.println(turnNumber + ": A* failed. No move was returned");
            return Move.STRAIGHT;
        }

    }

    static int calculateRisk(Coord c, GameSnake[] otherSnakes, Board board) {
        //find distance to closest snake within 10 of us
        int closestSnakeDist = Integer.MAX_VALUE;
        for (GameSnake s: otherSnakes) {
            Coord otherHeadPos = s.getHeadPos();
            int dist = board.distBetween(c, otherHeadPos);
            if (dist < closestSnakeDist) closestSnakeDist = dist;

        }

        int risk = 8 - closestSnakeDist;
        if (risk < 1) risk = 1;
        return risk;
    }
}
