public sealed interface Cell {
    char toChar();
    boolean isSafe();
    record EmptyCell(int risk) implements Cell {
        @Override
        public char toChar() {
            return ' ';
        }
        public boolean isSafe() { return true; }
    }
    record SnakeCell(boolean isUs, boolean isHead) implements Cell {
        public char toChar() {
            if (isHead && isUs) {
                return '$';
            }
            if (isHead) {
                return '%';
            } else {
                return '#';
            }
        }
        public boolean isSafe() { return false; }

    }
}
