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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Coord other = (Coord)o;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", x, y);
    }
}
