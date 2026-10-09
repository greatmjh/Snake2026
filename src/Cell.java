public sealed interface Cell {
    char toChar();
    record EmptyCell() implements Cell {
        @Override
        public char toChar() {
            return ' ';
        }
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
    }
}
