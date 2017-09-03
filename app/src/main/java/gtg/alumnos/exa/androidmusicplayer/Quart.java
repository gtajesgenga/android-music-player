package gtg.alumnos.exa.androidmusicplayer;

/**
 * Created by gus on 02/09/17.
 */

public class Quart<F, S, T, Q> {
    public final F first;
    public final S second;
    public final T third;
    public final Q fourth;

    /**
     * Constructor for a Tern.
     *
     * @param first the first object in the Quart
     * @param second the second object in the Quart
     * @param third the third object in the Quart
     * @param fourth the fourth object in the Quart
     */
    public Quart(F first, S second, T third, Q fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Quart)) {
            return false;
        }
        Quart<?, ?, ?, ?> t = (Quart<?, ?, ?, Quart>) o;
        return objectsEqual(t.first, first) && objectsEqual(t.second, second) && objectsEqual(t.third, third) && objectsEqual(t.fourth, fourth);
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
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode()) ^ (third == null ? 0 : third.hashCode()) ^ (fourth == null ? 0 : fourth.hashCode());
    }

    /**
     * Convenience method for creating an appropriately typed tern.
     * @param a the first object in the Tern
     * @param b the second object in the Tern
     * @param c the third object in the Tern
     * @return a Tern that is templatized with the types of a and b and c
     */
    public static <A, B, C, D> Quart <A, B, C, D> create(A a, B b, C c, D d) {
        return new Quart<A, B, C, D>(a, b, c, d);
    }
}
