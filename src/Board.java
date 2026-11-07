import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Board {
    public final int width, height;
    private final Cell[][] cells;

    //Basic initialiser
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        //initialise cells
        cells = new Cell[width][height];
        //set all to empty
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                setCell(new Coord(i, j), new Cell.FreeCell(false));
            }
        }
    }

    //full initialiser
    public Board(int width, int height, Coord applePos, GameSnake us, GameSnake[] others, int timeSinceLastApple) {
        this(width, height);
        //draw our snake
        addSnake(us, true, timeSinceLastApple);

        //draw other snakes
        for (GameSnake other : others) {
            if (other.alive) {
                addSnake(other, false, timeSinceLastApple);
            }
        }

        //add apple
        setCell(applePos, new Cell.FreeCell(true));
    }

    public void print() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(getCell(new Coord(j, i)).toChar() + " ");
            }
            System.out.println();
        }
    }

    //Getters and setters for cells
    public Cell getCell(Coord c) {
        Coord cw = wrap(c);
        return cells[cw.x][cw.y];
    }

    public void setCell(Coord c, Cell toSet) {
        Coord cw = wrap(c);
        cells[cw.x][cw.y] = toSet;
    }

    public int distBetween(Coord a, Coord b) {
        int Xdist = shortestWrapDistance(a.x, b.x, width);
        int Ydist = shortestWrapDistance(a.y, b.y, height);
        return Xdist + Ydist; //manhattan distance
    }

    public Coord coordFromMove(Coord fromCoord, Move m) {
        return switch (m) {
            case UP -> wrap(new Coord(fromCoord.x, fromCoord.y - 1));
            case DOWN -> wrap(new Coord(fromCoord.x, fromCoord.y + 1));
            case LEFT -> wrap(new Coord(fromCoord.x - 1, fromCoord.y));
            case RIGHT -> wrap(new Coord(fromCoord.x + 1, fromCoord.y));
            default -> null;
        };
    }

    public List<Tuple<Move, Coord>> movesFrom(Coord fromCoord) {
        return List.of(new Tuple<>(Move.UP, coordFromMove(fromCoord, Move.UP)),
                new Tuple<>(Move.DOWN, coordFromMove(fromCoord, Move.DOWN)),
                new Tuple<>(Move.LEFT, coordFromMove(fromCoord, Move.LEFT)),
                new Tuple<>(Move.RIGHT, coordFromMove(fromCoord, Move.RIGHT)));
    }

    public ArrayList<Tuple<Move, Coord>> safeMovesFrom(Coord fromCoord) {
        //all possible moves
        List<Tuple<Move, Coord>> possibleMoves = movesFrom(fromCoord);

        //find safe moves
        ArrayList<Tuple<Move, Coord>> safeMoves = new ArrayList<>();
        for (Tuple<Move, Coord> a : possibleMoves) {
            if (getCell(a.y()).getClass() == Cell.FreeCell.class) safeMoves.add(a);
        }
        return safeMoves;
    }

    public ArrayList<Tuple<Move, Coord>> futureSafeMovesFrom(Coord fromCoord, int depth) {
        //all possible moves
        List<Tuple<Move, Coord>> possibleMoves = movesFrom(fromCoord);

        //find safe moves
        ArrayList<Tuple<Move, Coord>> safeMoves = new ArrayList<>();
        for (Tuple<Move, Coord> a : possibleMoves) {
            if (getCell(a.y()).getClass() == Cell.FreeCell.class) safeMoves.add(a);
            if (getCell(a.y()).getClass() == Cell.SnakeCell.class && ((Cell.SnakeCell)(getCell(a.y()))).turnsUntilClear() < depth) safeMoves.add(a);
        }
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

    //internal methods
    Coord wrap(Coord c) {
        Coord result = new Coord(c.x, c.y);
        if (result.x >= 0) {
            result.x %= (width);
            if (result.x >= width) System.err.println("61");
        } else {
            result.x = width - ((-result.x) % (width - 1));
            if (result.x >= width) System.err.println("64");
        }
        if (result.y >= 0) {
            result.y %= (height);
            if (result.y >= height) System.err.println("68");
        } else {
            result.y = height - ((-result.y) % (height - 1));
            if (result.y >= height) System.err.println("71");

        }
        return result;
    }


    void addSnake(GameSnake snake, boolean isUs, int timeSinceLastApple) {
        int runningTimeToClear = snake.length;
        if (isUs && timeSinceLastApple < 5) {
            runningTimeToClear += (5 - timeSinceLastApple);
        }
        //Draw the snake
        for (int i = 1; i < snake.coords.size(); i++) {
            //check for repeated coord
            if (snake.coords.get(i).equals(snake.coords.get(i-1))) {
                if (getCell(snake.coords.get(i)).getClass() == Cell.FreeCell.class) {
                    setCell(snake.coords.get(i), new Cell.SnakeCell(isUs, false, runningTimeToClear));
                    runningTimeToClear --;
                }
                i++;
            } else {
                //Start and end coords
                Coord start = snake.coords.get(i-1);
                Coord end = snake.coords.get(i);


                //Which direction does this segment go?
                if (start.x < end.x) {
                    //left to right
                    for (int x = start.x; x <= end.x; x++) {
                        Coord c = new Coord(x, start.y);
                        if (getCell(c).getClass() == Cell.FreeCell.class) {
                            setCell(c, new Cell.SnakeCell(isUs, false, runningTimeToClear));
                            runningTimeToClear --;
                        }
                    }
                } else if (start.x > end.x) {
                    //right to left
                    for (int x = start.x; x >= end.x; x--) {
                        Coord c = new Coord(x, start.y);
                        if (getCell(c).getClass() == Cell.FreeCell.class) {
                            setCell(c, new Cell.SnakeCell(isUs, false, runningTimeToClear));
                            runningTimeToClear --;
                        }
                    }
                } else if (start.y < end.y) {
                    //up to down
                    for (int y = start.y; y <= end.y; y++) {
                        Coord c = new Coord(start.x, y);
                        if (getCell(c).getClass() == Cell.FreeCell.class) {
                            setCell(c, new Cell.SnakeCell(isUs, false, runningTimeToClear));
                            runningTimeToClear --;
                        }
                    }
                } else if (start.y > end.y) {
                    for (int y = start.y; y >= end.y; y--) {
                        Coord c = new Coord(start.x, y);
                        if (getCell(c).getClass() == Cell.FreeCell.class) {
                            setCell(c, new Cell.SnakeCell(isUs, false, runningTimeToClear));
                            runningTimeToClear --;
                        }
                    }
                } else {
                    throw new RuntimeException("Invalid snake coordinate pairs");
                }
            }

        }

        //Mark the head
        setCell(snake.getHeadPos(), new Cell.SnakeCell(isUs, true, snake.length));
    }

    static int shortestWrapDistance(int a, int b, int k) {
        int directDist = Math.abs(a - b);
        //wrap through the right side of the line
        int rightWrap = Math.abs(a - (b + k));
        //wrap through left side of the line
        int leftWrap = Math.abs(b - (a + k));

        return Math.min(Math.min(directDist, rightWrap), leftWrap);
    }

}
