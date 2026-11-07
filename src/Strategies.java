import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Strategies {
    public static Move adaptive(GameState gs) {

        //check if there is anything safe
        if (gs.board().safeMovesFrom(gs.ourSnake().getHeadPos()).isEmpty()) {
            System.out.printf("log %d: no safe moves, going straight\n", Snake.turnNumber);
            return Move.STRAIGHT;
        }

        //check if the apple is unsafe
        boolean appleUnsafe = contestedAppleCheck(gs);
        HashSet<Coord> unsafeCells = new HashSet<>();
        if (appleUnsafe) unsafeCells.add(gs.applePos());

        if (!appleUnsafe) { //only try to navigate to the apple if we won't have a head-head eating it
            Move moveToApple = aStar(gs, gs.ourSnake().getHeadPos(), gs.applePos(), false, unsafeCells);
            if (moveToApple != null && checkMoveSafety(gs, moveToApple, unsafeCells)) {
                System.out.printf("log %d: direct astar to apple\n", Snake.turnNumber);
                return moveToApple;
            }
        }

        //apple isn't safely reachable, try chasing our tail
        Move chaseTail = aStar(gs, gs.ourSnake().getHeadPos(), gs.ourSnake().getTailPos(), true, unsafeCells);
        if (chaseTail != null) {
            System.out.printf("log %d: apple not safely reachable, chasing tail\n", Snake.turnNumber);
            return chaseTail;
        }
        //chasing tail isn't viable, fallback to claustrophobic
        System.out.printf("log %d: tail not safely reachable, falling back to claustrophobic\n", Snake.turnNumber);
        return claustrophobic(gs, gs.ourSnake().getHeadPos(), unsafeCells);
    }

    static boolean checkMoveSafety(GameState gs, Move m, HashSet<Coord> unsafeCells) {
        return aStar(gs, gs.board().coordFromMove(gs.ourSnake().getHeadPos(), m), gs.ourSnake().getTailPos(), true, unsafeCells) != null;
    }

    //Checks if we could have a head collision when eating an apple
    static boolean contestedAppleCheck(GameState gs) {
        if (gs.board().distBetween(gs.applePos(), gs.ourSnake().getHeadPos()) > 1) return false; //we're not near the apple, no problem
        for (GameSnake s: gs.otherSnakes()) {
            if (gs.board().distBetween(gs.applePos(), s.getHeadPos()) <= 1 && s.length >= gs.ourSnake().length) return true;
        }
        return false;
    }

    public static Move aStar(GameState gs, Coord fromCoord, Coord toCoord, boolean searchAdjToo, HashSet<Coord> blockedCoords) {
        //working queue
        PriorityQueue<AstarEntry> pq = new PriorityQueue<>();

        //finalised nodes
        HashSet<AstarEntry> finalised = new HashSet<>();

        //add source vertex
        AstarEntry source = new AstarEntry(fromCoord, null, 0, gs.board().distBetween(fromCoord, toCoord), null);
        pq.add(source);

        AstarEntry result = null;
        //main loop
        while (!pq.isEmpty()) {
            //finalise the top of the PQ
            AstarEntry head = pq.remove();
            //check if we have hit the apple
            if ((searchAdjToo && gs.board().distBetween(head.location(), toCoord) <= 1) || head.location().equals(toCoord)) {
                result = head;
                break;
            }
            //check if already finalised
            if (finalised.contains(head)) continue;

            //add to finalised array
            finalised.add(head);

            //find depth of head and note all squares we've used
            int depth = 0;
            HashSet<Coord> treaded = new HashSet<>();
            AstarEntry curr = head;
            while (curr.prev() != null) {
                treaded.add(curr.location());
                curr = curr.prev();
                depth++;
            }

            ArrayList<Tuple<Move, Coord>> adjacencies = gs.board().futureSafeMovesFrom(head.location(), depth);
            for (Tuple<Move, Coord> x : adjacencies) {
                if (treaded.contains(x.y()) || blockedCoords.contains(x.y())) continue; //don't consider squares we used to get here safe
                Coord c = x.y();
                int risk = calculateRisk(c, gs.otherSnakes(), gs.board(), gs.ourSnake().length);
                int dist = gs.board().distBetween(c, toCoord);
                AstarEntry newEntry = new AstarEntry(c, head, head.cumulativeCost() + risk, dist, x.x());
                pq.add(newEntry);
                if (newEntry.location().equals(toCoord)) result = newEntry;
            }
        }

        //now trace path back from apple
        if (result == null) {
            return null;
        }
        if (result.location().equals(fromCoord)) return null;
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

        return switch (closestSnakeDist) {
            case 0 -> 100;
            case 1 -> 20;
            case 2 -> 8;
            case 3 -> 2;
            default -> 1;
        };
    }

    public static Move claustrophobic(GameState gs, Coord fromCoord, HashSet<Coord> unsafeCells) {
        ArrayList<Tuple<Move, Coord>> safeMoves = gs.board().safeMovesFrom(fromCoord);
        if (safeMoves.isEmpty()) return Move.STRAIGHT;
        //find moves where we can actually reach our tail
        ArrayList<Tuple<Move, Coord>> tailReachingMoves = new ArrayList<>();
        for (Tuple<Move, Coord> a : safeMoves) {
            if (aStar(gs, a.y(), gs.ourSnake().getTailPos(), true, unsafeCells) != null) tailReachingMoves.add(a);
        }
        if (!tailReachingMoves.isEmpty()) safeMoves = tailReachingMoves;

        int bestSpace = 0;
        Move bestMove = Move.STRAIGHT;
        for (Tuple<Move, Coord> x: safeMoves) {
            int currSpace = gs.board().reachableDoubleSafeFrom(x.y(), 120).size();
            if (currSpace > bestSpace && !unsafeCells.contains(x.y())) {
                bestMove = x.x();
            }
        }
        return bestMove;
    }
}
