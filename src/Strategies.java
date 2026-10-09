import java.util.ArrayList;
import java.util.PriorityQueue;

public class Strategies {
    public static Move aStar(Board board, Coord headPos, Coord applePos, GameSnake[] otherSnakes) {

        //working queue
        PriorityQueue<AstarEntry> pq = new PriorityQueue<>();

        //finalised nodes
        ArrayList<AstarEntry> finalised = new ArrayList<>();

        //add source vertex
        AstarEntry source = new AstarEntry(headPos, null, 0, board.distBetween(headPos, applePos));
        pq.add(source);

        //main loop
        while (!pq.isEmpty()) {
            //finalise the top of the PQ
            AstarEntry head = pq.remove();
            //check if already finalised
            boolean duplicate = false;
            for (AstarEntry fin: finalised) {
                if (head.location().equals(fin.location())) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) continue;

            //add to finalised array
            finalised.add(head);

            ArrayList<Coord> adjacencies = board.safeMovesFrom(head.location());
            for (Coord c : adjacencies) {
                int risk = calculateRisk(c, otherSnakes, board);
                int dist = board.distBetween(c, applePos);
                AstarEntry newEntry = new AstarEntry(c, head, head.cumulativeCost() + risk, dist);
                if (newEntry.location().equals(applePos)) { //found apple, we are done
                    finalised.add(newEntry);
                    pq.clear();
                    break;
                } else {
                    pq.add(newEntry);
                }
            }
        }

        //now trace path back from apple

        try {
            AstarEntry curr = finalised.getLast();
            while (!curr.prev().equals(source)) {
                curr = curr.prev();
            }
            //now curr is the first node in the chain
            if (curr.location().x > headPos.x) {
                return Move.RIGHT;
            } else if (curr.location().x < headPos.x) {
                return Move.LEFT;
            } else if (curr.location().y > headPos.y) {
                return Move.DOWN;
            } else {
                return Move.UP;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
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

        int risk = 5 - (closestSnakeDist*closestSnakeDist)/4;
        if (risk < 1) risk = 1;
        return risk;
    }
}
