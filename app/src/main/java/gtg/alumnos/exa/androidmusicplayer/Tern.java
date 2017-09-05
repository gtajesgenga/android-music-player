package gtg.alumnos.exa.androidmusicplayer;

/**
 * Created by gus on 02/09/17.
 */

public class Tern<F, S, T> {
    public final F first;
    public final S second;
    public final T third;

    /**
     * Constructor for a Tern.
     *
     * @param first the first object in the Quart
     * @param second the second object in the Quart
     * @param third the third object in the Quart
     */
    public Tern(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tern)) {
            return false;
        }
        Tern<?, ?, ?> t = (Tern<?, ?, ?>) o;
        return objectsEqual(t.first, first) && objectsEqual(t.second, second) && objectsEqual(t.third, third);
    }

    private static boolean objectsEqual(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Tern
     */
    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode()) ^ (third == null ? 0 : third.hashCode());
    }

    /**
     * Convenience method for creating an appropriately typed tern.
     * @param a the first object in the Tern
     * @param b the second object in the Tern
     * @param c the third object in the Tern
     * @return a Tern that is templatized with the types of a and b and c
     */
    public static <A, B, C> Tern<A, B, C> create(A a, B b, C c) {
        return new Tern<A, B, C>(a, b, c);
    }
}
