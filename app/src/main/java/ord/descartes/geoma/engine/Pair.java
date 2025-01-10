package ord.descartes.geoma.engine;

public class Pair<F, S> {
    protected final F first;
    protected final S second;

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
