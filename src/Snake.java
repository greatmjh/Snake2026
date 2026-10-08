//Competition entry point -- handles very basic input parsing

import java.io.*;
import java.util.*;

public class Snake {
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

            //Parse apple line
            String[] apple = line.split("\\s+");
            int appleX = Integer.parseInt(apple[0]), appleY = Integer.parseInt(apple[1]);
            Coord applePos = new Coord(appleX, appleY);

            //Parse me line
            int me = Integer.parseInt(in.readLine().trim());

            //Load snakes
            GameSnake ourSnake = null;
            GameSnake[] otherSnakes = new GameSnake[snakeCount - 1];
            int j = 0;
            for (int i = 0; i < snakeCount; i++) {
                if (i == me) {
                    ourSnake = new GameSnake(in.readLine());
                } else {
                    otherSnakes[j] = new GameSnake(in.readLine());
                    j++;
                }
            }

            Board board = new Board(width, height, applePos, ourSnake, otherSnakes);

            Move move = Strategies.naive(board, ourSnake.getHeadPos(), applePos);

            //Output
            move.print();
        }
    }
}