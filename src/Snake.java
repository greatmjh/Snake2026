//Competition entry point -- handles very basic input parsing

import java.io.*;
import java.util.*;

public class Snake {
    public static int turnNumber = 0;
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String[] init = in.readLine().trim().split("\\s+");
        int snakeCount = Integer.parseInt(init[0]);
        int width = Integer.parseInt(init[1]);
        int height = Integer.parseInt(init[2]);


        String line;
        while ((line = in.readLine()) != null) {
            //Handle edge cases and game over
            line = line.trim();
            if (line.equals("Game Over")) return;
            if (line.isEmpty()) continue;

            //Increment turn number
            turnNumber ++;
            //Parse apple line
            String[] apple = line.split("\\s+");
            int appleX = Integer.parseInt(apple[0]), appleY = Integer.parseInt(apple[1]);
            Coord applePos = new Coord(appleX, appleY);

            //Parse me line
            int me = Integer.parseInt(in.readLine().trim());

            //Load snakes
            GameSnake ourSnake = null;
            GameSnake[] otherSnakes = new GameSnake[snakeCount - 1];
            String[] snakeStrings = new String[snakeCount];
            int j = 0;
            for (int i = 0; i < snakeCount; i++) {
                snakeStrings[i] = in.readLine();
                if (i == me) {
                    ourSnake = new GameSnake(snakeStrings[i]);
                } else {
                    otherSnakes[j] = new GameSnake(snakeStrings[i]);
                    j++;
                }
            }

            Board board = new Board(width, height, applePos, ourSnake, otherSnakes);
            GameState gs = new GameState(board, ourSnake, otherSnakes, applePos);

            try {
                Move move = Strategies.adaptive(gs);
                //Output
                move.print();
            } catch (RuntimeException r) {
                System.err.println("--- A* failed ---");
                System.err.println("Turn number: " + turnNumber);
                System.err.println("Input:");
                System.err.printf("%d %d %d 1\n", snakeCount, width, height);
                System.err.printf("%d %d\n", appleX, appleY);
                System.err.println(me);
                for (String s : snakeStrings) {
                    System.err.println(s);
                }
                r.printStackTrace();
                System.exit(1);
            }
        }
    }
}