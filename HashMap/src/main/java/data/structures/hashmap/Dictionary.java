package data.structures.hashmap;

import java.util.Iterator;

// περιέχει χρήσιμες μεθόδους για την επεξεργασία λεξικών και παρόμοιων δομών δεδομένων
public interface Dictionary<K, V> extends Iterable<Dictionary.Entry<K, V>> {

    void put(K key, V value); // εισαγωγή στοιχείου

    V remove(K key); // διαγραφή στοιχείου

    V get(K key); // αναζήτηση στοιχείου

    boolean contains(K key); // επιστρέφει true αν το στοιχείο υπάρχει στο λεξικό, διαφορετικά false

    boolean isEmpty(); // επιστέφει true αν το λεξικό είναι άδειο, διαφορετικά false

    int size(); // επιστρέφει το μέγεθος του λεξικού

    void clear(); // αδειάζει το λεξικό

    Iterator<Entry<K, V>> iterator(); // επιστέφει έναν iterator για τη διάσχυση στο λεξικό

    interface Entry<K, V> { // υλοποιεί ένα στοιχείο

        K getKey(); // επιστρέφει το κλειδί

        V getValue(); // επιστρέφει την τιμή του
    }
}
