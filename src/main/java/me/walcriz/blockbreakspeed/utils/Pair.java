package me.walcriz.blockbreakspeed.utils;

/**
 * A pair utility for storing 2 elements
 * @param <T> The key data type
 * @param <K> The value data type
 * @author bl19
 * @see <a href="https://smmlive.se">smmlive.se</a>
 */
public class Pair<T,K> {

    /**
     * Creates a empty pair
     */
    public Pair() {  }

    /**
     * Creates a pair with the given key and value
     * @param tv The key
     * @param kv The value
     */
    public Pair(T tv, K kv) {
        fst = tv;
        snd = kv;
    }

    T fst;
    K snd;

    /**
     * Gets the key
     * @return The key
     */
    public T getKey() {
        return fst;
    }

    /**
     * Gets the value
     * @return The value
     */
    public K getValue() {
        return snd;
    }

    /**
     * Sets the key
     * @param key The key
     */
    public void setKey(T key) {
        this.fst = key;
    }

    /**
     * Sets the value
     * @param value The value
     */
    public void setValue(K value) {
        this.snd = value;
    }
}