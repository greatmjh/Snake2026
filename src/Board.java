import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Board {
    public final int width, height;
    private Cell[][] cells;

    //Basic initialiser
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        //initialise cells
        cells = new Cell[width][height];
        //set all to empty
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell.EmptyCell();
            }
        }
    }

    //full initialiser
    public Board(int width, int height, Coord applePos, GameSnake us, GameSnake[] others) {
        this(width, height);
        //draw our snake
        addSnake(us, true);

        //draw other snakes
        for (GameSnake other : others) {
            if (other.alive) {
                addSnake(other, false);
            }

        }
    }

    public Cell getCell(Coord c) {
        Coord cw = wrap(c);
        return cells[cw.x][cw.y];
    }

    public void setCell(Coord c, Cell toSet) {
        Coord cw = wrap(c);
        cells[cw.x][cw.y] = toSet;
    }

    public void print() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(cells[j][i].toChar() + " ");
            }
            System.out.println();
        }
    }

    public int distBetween(Coord a, Coord b) {
        int Xdist = shortestWrapDistance(a.x, b.x, width);
        int Ydist = shortestWrapDistance(a.y, b.y, height);
        return Xdist + Ydist; //manhattan distance
    }

    public List<Tuple<Move, Coord>> movesFrom(Coord fromCoord) {
        Coord up = wrap(new Coord(fromCoord.x, fromCoord.y - 1));
        Coord down = wrap(new Coord(fromCoord.x, fromCoord.y + 1));
        Coord left = wrap(new Coord(fromCoord.x - 1, fromCoord.y));
        Coord right = wrap(new Coord(fromCoord.x + 1, fromCoord.y));

        return List.of(new Tuple<>(Move.UP, up),
                new Tuple<>(Move.DOWN, down),
                new Tuple<>(Move.LEFT, left),
                new Tuple<>(Move.RIGHT, right));
    }

    public Coord coordFromMove(Coord fromCoord, Move m) {
        Coord up = wrap(new Coord(fromCoord.x, fromCoord.y - 1));
        Coord down = wrap(new Coord(fromCoord.x, fromCoord.y + 1));
        Coord left = wrap(new Coord(fromCoord.x - 1, fromCoord.y));
        Coord right = wrap(new Coord(fromCoord.x + 1, fromCoord.y));
        return switch (m) {
            case UP -> up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
            default -> null;
        };

    }

    public ArrayList<Tuple<Move, Coord>> safeMovesFrom(Coord fromCoord) {
        //translated coords
        Coord up = wrap(new Coord(fromCoord.x, fromCoord.y - 1));
        Coord down = wrap(new Coord(fromCoord.x, fromCoord.y + 1));
        Coord left = wrap(new Coord(fromCoord.x - 1, fromCoord.y));
        Coord right = wrap(new Coord(fromCoord.x + 1, fromCoord.y));

        //find safe moves
        ArrayList<Tuple<Move, Coord>> safeMoves = new ArrayList<>();
        //check up
        if (getCell(up).getClass() == Cell.EmptyCell.class) safeMoves.add(new Tuple<>(Move.UP, up));
        //check down
        if (getCell(down).getClass() == Cell.EmptyCell.class) safeMoves.add(new Tuple<>(Move.DOWN, down));
        //check left
        if (getCell(left).getClass() == Cell.EmptyCell.class) safeMoves.add(new Tuple<>(Move.LEFT, left));
        //check right
        if (getCell(right).getClass() == Cell.EmptyCell.class) safeMoves.add(new Tuple<>(Move.RIGHT, right));

        return safeMoves;
    }

    public ArrayList<Tuple<Move, Coord>> doubleSafeMovesFrom(Coord fromCoord) {
        ArrayList<Tuple<Move, Coord>> doubleSafeMoves = new ArrayList<>();
        //find safe moves
        ArrayList<Tuple<Move, Coord>> safeMoves = safeMovesFrom(fromCoord);
        for (Tuple<Move, Coord> x: safeMoves) {
            if (safeMovesFrom(x.y()).size() == 4) {
                doubleSafeMoves.add(x);
            }
        }

        return doubleSafeMoves;
    }

    public HashSet<Coord> reachableDoubleSafeFrom(Coord fromCoord, int limit) {
        HashSet<Coord> result = new HashSet<>();
        ArrayDeque<Coord> workingQueue = new ArrayDeque<>();
        workingQueue.add(fromCoord);
        int i = 0;
        while (!workingQueue.isEmpty() && i++ < limit) {
            Coord head = workingQueue.poll();
            if (result.contains(head)) continue;
            result.add(head);
            ArrayList<Tuple<Move, Coord>> doubleSafeFromHere = doubleSafeMovesFrom(head);
            for (Tuple<Move, Coord> x: doubleSafeFromHere) {
                workingQueue.add(x.y());
            }
        }
        return result;
    }

    public Coord wrap(Coord c) {
        Coord result = new Coord(c.x, c.y);
        if (result.x >= 0) {
            result.x %= (width);
            if (result.x >= width) System.err.println("61");
        } else {
            result.x = width - ((-result.x) % (width - 1)) - 1;
            if (result.x >= width) System.err.println("64");
        }
        if (result.y >= 0) {
            result.y %= (height);
            if (result.y >= height) System.err.println("68");
        } else {
            result.y = height - ((-result.y) % (height - 1)) - 1;
            if (result.y >= height) System.err.println("71");

        }
        return result;
    }

    void drawLine(Coord start, Coord end, Cell cellType) {
        //Find min and max
        int minX = Math.min(start.x, end.x);
        int minY = Math.min(start.y, end.y);
        int maxX = Math.max(start.x, end.x);
        int maxY = Math.max(start.y, end.y);

        //Fill the area
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                cells[i][j] = cellType;
            }
        }
    }


    public void addSnake(GameSnake snake, boolean isUs) {
        //Draw the snake
        for (int i = 1; i < snake.coords.size(); i++) {
            //check for repeated coord
            if (snake.coords.get(i).equals(snake.coords.get(i-1))) {
                i++;
                continue;
            }
            drawLine(snake.coords.get(i-1), snake.coords.get(i), new Cell.SnakeCell(isUs, false));
        }

        //Mark the head
        cells[snake.getHeadPos().x][snake.getHeadPos().y] = new Cell.SnakeCell(isUs, true);
    }

    static int shortestWrapDistance(int a, int b, int k) {
        int directDist = Math.abs(a - b);
        //wrap through the right side of the line
        int rightWrap = Math.abs(a - (b + k));
        //wrap through left side of the line
        int leftWrap = Math.abs(a - (k - b));

        return Math.min(Math.min(directDist, rightWrap), leftWrap);
    }

}
