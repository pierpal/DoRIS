package doris;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public abstract class Grid {

    abstract boolean hasNext();

    abstract double[] nextSet();

    abstract void resetIterator();

}
