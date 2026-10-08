import java.util.ArrayList;

public class Strategies {
    public static Move naive(Board board, Coord headPos, Coord applePos) {

        //find safe moves
        ArrayList<Move> safeMoves = new ArrayList<>();

        //check up
        if (board.getCell(new Coord(headPos.x, headPos.y - 1)).isSafe()) safeMoves.add(Move.UP);
        //check down
        if (board.getCell(new Coord(headPos.x, headPos.y + 1)).isSafe()) safeMoves.add(Move.DOWN);
        //check left
        if (board.getCell(new Coord(headPos.x-1, headPos.y)).isSafe()) safeMoves.add(Move.LEFT);
        //check right
        if (board.getCell(new Coord(headPos.x+1, headPos.y)).isSafe()) safeMoves.add(Move.RIGHT);

        if (safeMoves.isEmpty()) return Move.STRAIGHT; //we are cooked

        //Find the best move
        int bestDist = Integer.MAX_VALUE;
        Move bestMove = Move.STRAIGHT;

        int dist;
        for (Move m: safeMoves) {
            switch(m) {
                case UP:
                    dist = applePos.y - headPos.y;
                    if (dist == 0) break; //already at the level, no need to move
                    if (dist < 0) dist -= board.height; //wrap

                    if (dist < bestDist) {
                        bestDist = dist;
                        bestMove = m;
                    }
                    break;
                case DOWN:
                    dist = headPos.y - applePos.y;
                    if (dist == 0) break; //already at the level, no need to move
                    if (dist < 0) dist -= board.height; //wrap

                    if (dist < bestDist) {
                        bestDist = dist;
                        bestMove = m;
                    }
                    break;
                case LEFT:
                    dist = applePos.x - headPos.x;
                    if (dist == 0) break; //already at the level, no need to move
                    if (dist < 0) dist -= board.width; //wrap

                    if (dist < bestDist) {
                        bestDist = dist;
                        bestMove = m;
                    }
                    break;
                case RIGHT:
                    dist = headPos.x - applePos.x;
                    if (dist == 0) break; //already at the level, no need to move
                    if (dist < 0) dist -= board.width; //wrap

                    if (dist < bestDist) {
                        bestDist = dist;
                        bestMove = m;
                    }
                    break;
            }
        }

        return bestMove;
    }
}
