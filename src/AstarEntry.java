public record AstarEntry(Coord location, AstarEntry prev, int cumulativeCost, int dist) implements Comparable<AstarEntry> {
    public int rankValue() {
        return cumulativeCost + dist;
    }

    @Override
    public int compareTo(AstarEntry o) {
        return Integer.compare(this.rankValue(), o.rankValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        AstarEntry other = (AstarEntry) o;
        return this.location().equals(other.location());
    }

    @Override
    public int hashCode() {
        return 100*location.x + location.y;
    }
}
