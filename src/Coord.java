import java.util.InputMismatchException;

public class Coord {
    public int x, y;
    public Coord(String inStr) { //Coord from string "x,y"
        String[] split = inStr.split(",");
        if (split.length != 2) throw new InputMismatchException("Coordinate must only have 2 values in x,y");
        try {
            x = Integer.parseInt(split[0]);
            y = Integer.parseInt(split[1]);
        } catch (NumberFormatException n) {
            throw new InputMismatchException("Coordinate must have integer values");
        }
    }

    public Coord(int x, int y) { //Simple constructor
        this.x = x;
        this.y = y;
    }
}
