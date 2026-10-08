public class Board {
    public final int width, height;
    private Cell[][] cells;

    //Basic initialiser
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        //initialise cells
        cells = new Cell[width][height];
        //set all to empty, risk 0
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell.EmptyCell(0);
            }
        }
    }

    //full initialiser
    public Board(int width, int height, Coord applePos, GameSnake us, GameSnake[] others) {
        this(width, height);

        //draw apple
        setCell(applePos, new Cell.Apple());

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

    private Coord wrap(Coord c) {
        Coord result = new Coord(c.x, c.y);
        if (result.x >= 0) {
            result.x %= (width - 1);
            if (result.x >= width) System.err.println("61");
        } else {
            result.x = width - ((-result.x) % (width - 1)) - 1;
            if (result.x >= width) System.err.println("64");
        }
        if (result.y >= 0) {
            result.y %= (height - 1);
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
            drawLine(snake.coords.get(i-1), snake.coords.get(i), new Cell.SnakeCell(isUs, false));
        }

        //Mark the head
        cells[snake.getHeadPos().x][snake.getHeadPos().y] = new Cell.SnakeCell(isUs, true);
    }

}
