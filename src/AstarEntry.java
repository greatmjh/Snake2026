public record AstarEntry(Coord location, AstarEntry prev, int cumulativeCost, int dist) implements Comparable<AstarEntry> {
    public int rankValue() {
        return cumulativeCost + dist;
    }

    @Override
    public int compareTo(AstarEntry o) {
        return Integer.compare(this.rankValue(), o.rankValue());
    }
}
