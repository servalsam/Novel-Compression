import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Hash Table data structure based on the Map interface, whereas a value is
 * stored and linked to a key. This implementation permits null keys and
 * null values. There is no guarantee of the order in the structure.
 *
 * @author Samuel Wainright, swain91@uw.edu
 * @version 24 May 2019
 */
class MyHashTable<K, V> {

    /** The capacity of the data structure. */
    private int capacity;

    /** The current count of the elements contained. */
    private int size;

    /** The maximum value of probes before a value is reached. */
    private int maxProbe;

    /** Stores probe successive probe count data. */
    private ArrayList<Integer> probeStats;

    /** Stores keys. */
    private K[] keys;

    /** Stores values. */
    private V[] values;

    /**
     * Constructor that accepts a capacity and initializes
     * the data structure to that limit. Keys and values are
     * initialized to null. The probeStats ArrayList is filled with
     * 0s.
     *
     * @param capacity The maximum number of entries possible.
     */
    @SuppressWarnings("unchecked")
    MyHashTable(int capacity) {

        this.capacity = capacity;
        size = 0;
        maxProbe = 0;
        probeStats = new ArrayList<>(capacity);
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];

        for (int i = 0; i < keys.length; i++) {
            keys[i] = null;
            values[i] = null;
            probeStats.add(0);
        }
    }

    // Use this in the future to prevent unchecked casts
    /*
    MyHashTable(Class<K[]> clazz1, Class<V[]> clazz2, int capacity) {

        this.capacity = capacity;
        size = 0;
        totalProbes = 0;
        maxProbe = 0;
        probeStats = new int[capacity];
        keys = clazz1.cast(Array.newInstance(clazz1.getComponentType(), capacity));
        values = clazz2.cast(Array.newInstance(clazz2.getComponentType(), capacity));
    }*/

    /**
     * The put() function stores a value associated with a given
     * key. If the index obtained by the hash() function is null,
     * the key and value is stored at that index. Otherwise, the key and value
     * are stored by linear probing. If the key is found within the array,
     * it overwrites the value at that location.
     *
     * @param searchKey The search key supplied.
     * @param newValue The value to be stored.
     */
    void put(K searchKey, V newValue) {
        int index = hash(searchKey);
        int count = 0;      // Stores probe count

        if (keys[index] == null) {
            // New entry without probe
            keys[index] = searchKey;
            values[index] = newValue;
            size++;
            Integer current = probeStats.get(0);
            probeStats.set(0, ++current);
        } else if (!keys[index].equals(searchKey)) {
            while (keys[index] != null) {
                ++index;
                count++;

                try {
                    if (keys[index].equals(searchKey)) {
                        // Key was found during probe
                        break;
                    }
                } catch (NullPointerException npe) {
                    // Null was reached, new entry
                    size++;
                    break;
                }

            }

            keys[index] = searchKey;
            values[index] = newValue;

            // Update probe statistics
            int current = probeStats.get(count);
            probeStats.set(count, ++current);
            maxProbe = Math.max(count, maxProbe);
        } else {
            values[index] = newValue;
        }
    }

    /**
     * The get() function returns a value paired
     * to the supplied search key.
     *
     * @param searchKey The supplied search key.
     * @return The value stored at the corresponding index of the search key.
     */
    V get(K searchKey) {
        int index = hash(searchKey);
        int origin = index; // Stores the original entry point
        V fetched = null;

        if (keys[index].equals(searchKey)) {
            fetched = values[index];
        } else {
            while (keys[index] != null) {
                if (keys[index].equals(searchKey))
                    //found
                    fetched = values[index];
                ++index;
                if (index == capacity)
                    index = 0;
                if (index == origin)
                    // Reached entry point after looping, key not there...
                    break;
            }
        }

        return fetched;
    }

    /**
     * The containsKey() function takes a supplied search
     * key and determines if the key is contained within the
     * data structure.
     *
     * @param searchKey The supplied search key.
     * @return The boolean flag, true if contained, false otherwise.
     */
    boolean containsKey(K searchKey) {
        int index = hash(searchKey);
        int origin = index;
        boolean found = false;

        if (keys[index] != null) {
            if (keys[index].equals(searchKey)) {
                found = true;
            } else {
                while (keys[index] != null) {
                    if (keys[index].equals(searchKey)) {
                        // Key was found during probe
                        found = true;
                    }
                    ++index;
                    if (index == capacity)
                        index = 0;
                    if (index == origin)
                        // not found...
                        break;

                }
            }
        }

        return found;
    }

    /**
     * The primary hash function that generates a
     * random index.
     *
     * @param key The key supplied.
     * @return The index generated.
     */
    private int hash(K key) {
        return Math.abs(key.hashCode() % capacity);
    }

    /**
     * The getSize() function returns the current
     * number of elements contained.
     *
     * @return The current number of elements contained in the data structure.
     */
    int getSize() {
        return size;
    }

    /**
     * The getKeys() function returns a copy ArrayList of all the keys
     * contained, ignoring all null keys.
     *
     * @return A copy ArrayList of all keys.
     */
    ArrayList<K> getKeys() {
        ArrayList<K> keyCopies = new ArrayList<>();
        for (K k :
                keys) {
            if (k != null) {
                keyCopies.add(k);
            }
        }

        return keyCopies;
    }

    /**
     * The getValues() function returns a copy of all the values
     * stored, ignoring all null values.
     *
     * @return A copy of the values stored.
     */
    ArrayList<V> getValues() {
        ArrayList<V> valuesCopy = new ArrayList<>();
        for (V v :
                values) {
            if (v != null) {
                valuesCopy.add(v);
            }
        }

        return valuesCopy;
    }

    /**
     * Displays the number of entries, the number of buckets,
     * a histogram of probes, the maximum linear probe values,
     * the percentage of the structure filled, and the average number
     * of probes needed to find values.
     *
     */
    void stats() {
        for (int i = probeStats.size() - 1; probeStats.get(i) == 0; i--) {
            probeStats.remove(i);
        }
        System.out.println("Hash Table Stats");
        System.out.println("===================");
        System.out.println("Number of Entries: " + size);
        System.out.println("Number of Buckets: " + capacity);
        System.out.println("Histogram of probes:");
        System.out.print("[");

        int count = 0;

        for (int i = 0; i < probeStats.size(); i++) {
            if (i != probeStats.size() - 1) {
                System.out.print(probeStats.get(i) + ", ");
            } else {
                System.out.print(probeStats.get(i));
            }
            if (i % 22 == 0 && i != 0) {
                System.out.println();
                System.out.print(" ");
            }
            if (probeStats.get(i) != 0) {
                count++;
            }
        }
        System.out.print("]");
        System.out.println();

        double percentage = ((double) size / (double) capacity) * 100.0;
        double average = ((double) maxProbe / (double) count);

        System.out.println("Max Linear Probe: " + maxProbe);
        System.out.println("Fill Percentage: " + (new BigDecimal(percentage).setScale(6, RoundingMode.HALF_EVEN) + "%"));
        System.out.println("Average Linear Probe: " + new BigDecimal(average).setScale(6, RoundingMode.HALF_EVEN));
        System.out.println();
    }
}
