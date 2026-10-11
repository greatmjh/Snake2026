import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

//Represents one of the individual snakes on the board
public class GameSnake {
    public boolean alive;
    public int length;
    public int kills;
    public ArrayList<Coord> coords;

    public Coord getHeadPos() {
        return coords.getFirst();
    }
    public Coord getTailPos() {
        return coords.getLast();
    }

    public GameSnake(String inputString) {
        Scanner sc = new Scanner(inputString);

        //Alive status
        String aliveStatus = sc.next();
        if (aliveStatus.equals("alive")) {
            alive = true;
        } else if (aliveStatus.equals("dead")) {
            alive = false;
        } else {
            throw new InputMismatchException("Invalid alive status");
        }

        //Length
        String strLength = sc.next();
        length = Integer.parseInt(strLength);

        //Kills
        String strKills = sc.next();
        kills = Integer.parseInt(strKills);

        //Coords
        coords = new ArrayList<>();
        while (sc.hasNext()) {
            coords.add(new Coord(sc.next()));
        }
    }

}
