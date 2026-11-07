import java.util.HashSet;

public class DbgUtils {
    public static void printFinalisedSquares(HashSet<AstarEntry> fin, Coord applePos, int w, int h) {
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                boolean finalised = fin.contains(new AstarEntry(new Coord(j, i), null, 0, 0, null));
                boolean isApple = applePos.equals(new Coord(j, i));
                if (finalised) {
                    if (isApple) {
                        System.out.print("A ");
                    } else {
                        System.out.print("X ");
                    }
                } else {
                    if (isApple) {
                        System.out.print("a ");
                    } else {
                        System.out.print("  ");
                    }
                }
            }
            System.out.println();
        }
    }

    public static void printPath(AstarEntry head, Coord applePos, int w, int h) {
        HashSet<Coord> treaded = new HashSet<>();
        AstarEntry curr = head;
        while (curr.prev() != null) {
            treaded.add(curr.location());
            curr = curr.prev();
        }
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                Coord c = new Coord(j, i);
                if (treaded.contains(c)) System.out.print("* ");
                else System.out.print("  ");
            }
            System.out.println();
        }
    }
}
