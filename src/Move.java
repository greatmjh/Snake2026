public enum Move {
    UP, DOWN, LEFT, RIGHT, REL_LEFT, STRAIGHT, REL_RIGHT;
    public void print() {
        switch (this) {
            case UP -> System.out.println("0");
            case DOWN -> System.out.println("1");
            case LEFT -> System.out.println("2");
            case RIGHT -> System.out.println("3");
            case REL_LEFT -> System.out.println("4");
            case STRAIGHT -> System.out.println("5");
            case REL_RIGHT -> System.out.println("6");
        }
        System.out.flush();
    }
}
