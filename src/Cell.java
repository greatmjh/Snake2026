public sealed interface Cell {
    char toChar();
    record FreeCell(boolean hasApple) implements Cell {
        @Override
        public char toChar() {
            if (hasApple) return '@';
            else return ' ';
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
