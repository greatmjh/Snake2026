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

            //Parse me line
            int me = Integer.parseInt(in.readLine().trim());

            //Load snakes -- TODO: parse into GameSnake class
            String[] snakes = new String[snakeCount];
            for (int i = 0; i < snakeCount; i++) snakes[i] = in.readLine();

            //TODO: select move

            //Output
            // 0 up, 1 down, 2 left, 3 right; 4 left, 5 straight, 6 right.
            System.out.println("0"); //TODO
            System.out.flush();
        }
    }
}