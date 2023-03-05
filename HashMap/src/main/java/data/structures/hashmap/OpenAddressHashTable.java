package data.structures.hashmap;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

// πίνακας κατακερματισμού με τη χρήση ανοικτής διευθυνσιοδότησης και γραμμικής διερεύνησης
public class OpenAddressHashTable<K, V> implements Dictionary<K, V> {

    // αρχικό μέγεθος του πίνακα κατακερματισμού
    private static final int DEFAULT_INITIAL_SIZE = 16;

    private Entry<K, V>[] array;    // πίνακας κατακερματισμού
    private int size;               // αριθμός στοιχείων αποθηκευμένα στον πίνακα
    private int[][] hashFunction;   // συνάρτηση κατακερματισμού

    // αρχικοποιεί τις μεταβλητές και διαλέγει νέα συνάρτηση κατακερματισμού
    @SuppressWarnings("unchecked")
    public OpenAddressHashTable(int m) {
        // αν το μέγεθος του πίνακα είναι αρνητικό εμφάνισε εξαίρεση
        if (m <= 0) {
            throw new IllegalArgumentException("Array size must be positive");
        }
        size = 0;                                                       // ο πίνακας αρχικά δεν περιέχει στοιχεία
        array = (Entry<K, V>[]) Array.newInstance(EntryImpl.class, m);  // δημιούργησε ένα πίνακα κατακερματισμού μεγέθους m
        generateHashFunction();                                         // διάλεξε νέα συνάρτηση κατακερματισμού
    }

    // δημιουργεί ένα πίνακα κατακερματισμού με προκαθορισμένο αρχικό μέγεθος
    public OpenAddressHashTable() {
        this(DEFAULT_INITIAL_SIZE);
    }

    // εισάγει ένα στοιχείο στον πίνακα κατακερματισμού
    @Override
    public void put(K key, V value) {
        rehashIfNeeded();   // κάνε επανακατακερματισμό αν κριθεί απαραίτητο
        insert(key, value); // πρόσθεσε το στοιχείο στον πίνακα
    }

    // διαγράφει ένα στοιχείο απ' τον πίνακα κατακερματισμού
    @Override
    public V remove(K key) {
        rehashIfNeeded();               // κάνε επανακατακερματισμό αν κριθεί απαραίτητο
        int element = getElement(key);  // πάρε την αρχική θέση του στοιχείου στον πίνακα

        // περνάει απ' όλα τα στοιχεία του πίνακα ξεκινόντας απ' τη θέση element
        for (int count = 0; count < array.length; count++, element++) {
            // αν το element ξεπεράσει το μέγεθος του πίνακα, το i ξεκινάει απ΄ το 0
            int i = element % array.length;

            // αν το κλειδί βρέθηκε στην θέση i
            if (array[i] != null && key.equals(array[i].getKey())) {
                V value = array[i].getValue();  // αποθήκευσε το value για να το επιστρέψεις στο τέλος
                array[i] = null;                // άφησε ένα κενό στη θέση του στοιχείου
                size--;                         // μείωσε τον αριθμό των στοιχείων κατά 1

                // περνάει απ' όλα τα στοιχεία του πίνακα - το j είναι πάντα μια θέση μεγαλύτερη απ' το i
                for (int k = 0, j = (i + 1) % array.length; k < array.length; k++) {
                    // αν το στοιχείο στη θέση j είναι κενό
                    if (array[j] == null) {
                        return value; // επέστρεψε το value και ολοκλήρωσε τη διαγραφή
                    }
                    // αν η συνάρτηση κατακερματισμού επιστρέφει τιμή μεγαλύτερη του i για το στοιχείο στη θέση j
                    else if (getElement(array[j].getKey()) > i) {
                        j = (j + 1) % array.length; // απλά αύξησε το j χωρίς να περάσεις τα όρια του πίνακα
                    }
                    // διαφορετικά αντιμετάθεσε τα στοιχεία στις θέσεις i και j
                    else if (getElement(array[j].getKey()) <= i) {
                        Entry<K, V> temp = array[i];
                        array[i] = array[j];
                        array[j] = temp;
                        j = (j + 1) % array.length; // αύξησε τον δείκτη j
                    }
                }
            }
        }
        return null; // αν η διαγραφή ήταν ανεπιτυχής επέστρεψε null
    }

    // αναζητά και επιστρέφει το value ενός στοιχείου απ' τον πίνακα κατακερματισμού
    @Override
    public V get(K key) {
        int element = getElement(key); // πάρε την αρχική θέση του στοιχείου στον πίνακα

        // περνάει απ' όλα τα στοιχεία του πίνακα ξεκινόντας απ' τη θέση element
        for (int count = 0; count < array.length; count++, element++) {
            // αν το element ξεπεράσει το μέγεθος του πίνακα, το i ξεκινάει απ΄ το 0
            int i = element % array.length;

            // αν το στοιχείο στη θέση i δεν είναι κενό και το κλειδί βρέθηκε σε αυτή τη θέση
            if (array[i] != null && key.equals(array[i].getKey())) {
                return array[i].getValue(); // επέστρεψε το value του στοιχείου
            }
        }
        return null; // αν το στοιχείο δεν βρέθηκε στον πίνακα επέστρεψε null
    }

    // επιστρέφει true ή false ανάλογα με το αν το στοιχείο υπάρχει στον πίνακα κατακερματισμού
    @Override
    public boolean contains(K key) {
        int element = getElement(key); // πάρε την αρχική θέση του στοιχείου στον πίνακα

        // περνάει απ' όλα τα στοιχεία του πίνακα ξεκινόντας απ' τη θέση element
        for (int count = 0; count < array.length; count++, element++) {
            // αν το element ξεπεράσει το μέγεθος του πίνακα, το i ξεκινάει απ΄ το 0
            int i = element % array.length;

            // αν το στοιχείο στη θέση i δεν είναι κενό και το κλειδί βρέθηκε σε αυτή τη θέση
            if (array[i] != null && key.equals(array[i].getKey())) {
                return true; // επέστρεψε true
            }
        }
        return false; // αν το στοιχείο δεν βρέθηκε στον πίνακα επέστρεψε false
    }

    // επιστρέφει true αν ο πίνακας δεν έχει στοιχεία διαφορετικά false
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // επιστρέφει τον αριθμό των στοιχείων αποθηκευμένα στον πίνακα κατακερματισμού
    @Override
    public int size() {
        return size;
    }

    // αδειάζει τον πίνακα κατακερματισμού
    @Override
    public void clear() {
        size = 0; // μηδενίζει τον αριθμό των στοιχείων αποθηκευμένα στον πίνακα
        for (int i = 0; i < array.length; i++) { // για κάθε στοιχείο στον πίνακα
            array[i] = null; // άφησε ένα κενό
        }
    }

    // επιστρέφει ένα νέο HashIterator για τη διέλευση του πίνακα κατακερματισμού
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new HashIterator();
    }

    // επιστρέφει τη θέση που πρέπει να έχει ένα στοιχείο στον πίνακα κατακερματισμού
    // με βάση το hashCode του κλειδιού και την συνάρτηση κατακερματισμού
    private int getElement(K key) {
        int rows = (int) (Math.log(array.length) / Math.log(2)); // αριθμός γραμμών συνάρτησης κατακερματισμού
        int columns = 32;           // αριθμός στηλών συνάρτησης κατακερματισμού
        int[] x = new int[columns]; // κάθε θέση του πίνακα κρατάει 1 από τα 32 bit του κλειδιού εισόδου
        int[] h = new int[rows];    // κρατάει τα μεμονωμένα bit του ακεραίου που χρησιμοποιείται για τη διευθυνσιοδότηση του πίνακα κατακερματισμού

        // απομονώνει τα bit που επιστρέφει η συνάρτηση hashCode και τα αποθηκεύει
        // ξεχωριστά στις 32 θέσεις του πίνακα x
        for (int i = 0; i < columns; i++) {
            // (α) η κλήση της συνάρτησης Math.abs(key.hashCode()) επιστρέφει έναν ακέραιο 32 bit
            // (β) κάθε φορά που τα bit μετατοπίζονται δεξιά, το i + 1 bit τοποθετείται στην λιγότερο σημαντική θέση
            // (γ) η μάσκα & 1 απομονώνει το συγκεκριμένο bit και το εκχωρεί στην θέση i του πίνακα x
            x[i] = (Math.abs(key.hashCode()) >> (i + 1)) & 1;
        }

        // για κάθε στοιχείο μιας γραμμής του δισδιάστατου πίνακα hashFunction
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                h[i] += hashFunction[i][j] * x[j];  // πολλαπλασίασε το στοιχείο με το αντίστοιχο του πίνακα x και πρόσθεσέ το στο αποτέλεσμα
                h[i] %= 2;                          // modulo 2 αριθμητική
            }
        }
        // αναπαριστά τον ακέραιο που θα χρησιμοποιηθεί για τη διευθυνσιοδότηση του πίνακα κατακερματισμού
        int element = 0;

        // ** μετατροπή του δυαδικού αριθμού που περιέχεται στον πίνακα h σε δεκαδικό **
        // για κάθε bit στον πίνακα h
        for (int i = 0; i < rows; i++) {
            element += Math.pow(2, i) * h[i]; // πολλαπλασίασε το i-οστό bit με την i-οστή δύναμη του 2 και πρόσθεσέ το στο αποτέλεσμα
        }

        return element; // επέστρεψε τον δεκαδικό
    }

    // εισάγει ένα στοιχείο στον πίνακα κατακερματισμού χωρίς να κάνει rehash
    private void insert(K key, V value) {
        int element = getElement(key); // πάρε την αρχική θέση του στοιχείου στον πίνακα

        // περνάει απ' όλα τα στοιχεία του πίνακα ξεκινόντας απ' τη θέση element
        for (int count = 0; count < array.length; count++, element++) {
            // αν το element ξεπεράσει το μέγεθος του πίνακα, το i ξεκινάει απ΄ το 0
            int i = element % array.length;

            // πρόσθεσε το στοιχείο ή ενημέρωσέ το με νέα τιμή
            if (array[i] == null || key.equals(array[i].getKey())) {
                array[i] = new EntryImpl<>(key, value); // γράψε το entry στην θέση i του πίνακα
                size++; // αύξησε τον αριθμό των στοιχείων κατά 1
                return;
            }
        }
    }

    // παράγει μια τυχαία συνάρτηση κατακερσματισμού
    private void generateHashFunction() {
        Random rand = new Random();                              // για την παραγωγή τυχαίων αριθμών
        int rows = (int) (Math.log(array.length) / Math.log(2)); // αριθμός γραμμών συνάρτησης κατακερματισμού
        int columns = 32;                                        // αριθμός στηλών συνάρτησης κατακερματισμού
        hashFunction = new int[rows][columns];                   // πίνακας b x u

        // σε κάθε στοιχείο του δισδιάστατου πίνακα hashFunction
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                hashFunction[i][j] = rand.nextInt(2); // αποθήκευσε έναν τυχαίο αριθμό 0 ή 1
            }
        }
    }

    // κάνει επανακατακερματισμό υπό ορισμένα κριτήρια
    private void rehashIfNeeded() {
        double avg = (double) size / array.length;  // ποσοστό που ο πίνακας έχει γεμίσει
        int newLength;                              // νέο μήκος του πίνακα

        // αν ο αριθμός των στοιχείων αποθηκευμένων στον πίνακα ξεπεράσει το 80% της χωρητικότητάς του
        if (avg > 0.8) {
            newLength = array.length * 2; // διπλασίασε το μέγεθος του πίνακα
        }
        // αν ο αριθμός των αντικειμένων γίνει μικρότερος του 25% της χωρητικότητας
        else if (avg < 0.25 && array.length > 2 * DEFAULT_INITIAL_SIZE) {
            newLength = array.length / 2; // υποδιπλασίασε τον πίνακα
        }
        else {
            return; // απλά επέστρεψε
        }

        // δημιούργησε νέο πίνακα κατακερματισμού καλώντας τη συνάρτηση δημιουργίας με
        // το νέο μέγεθος - κατά τη δημιουργία θα επιλεγεί νέα συνάρτηση κατακερματισμού
        OpenAddressHashTable<K, V> newHashTable = new OpenAddressHashTable<K, V>(newLength);

        // για κάθε entry στον παλιό πίνακα κατακερματισμού
        for (Entry<K, V> element : this) {
            newHashTable.insert(element.getKey(), element.getValue()); // πρόσθεσε τα στοιχεία του παλιού πίνακα στον καινούριο
        }

        this.array = newHashTable.array; // η μεταβλητή array δείχνει τώρα στον νέο πίνακα κατακερματισμού
        this.size = newHashTable.size; // αποθήκευσε στην size το νέο μέγεθος του πίνακα - περιττή πρόταση
        this.hashFunction = newHashTable.hashFunction; // η μεταβλητή hashFunction δείχνει στη νέα συνάρτηση κατακερματισμού
    }

    // υλοποιεί το interface Dictionary.Entry<K, V> για την αποθήκευση στοιχείων στον πίνακα κατακερματισμού
    private static class EntryImpl<K, V> implements Dictionary.Entry<K, V> {

        private K key;   // κλειδί
        private V value; // τιμή

        // αρχικοποιεί τις μεταβλητές
        public EntryImpl(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key; // επιστρέφει το κλειδί
        }

        @Override
        public V getValue() {
            return value; // επιστρέφει την τιμή
        }
    }

    // υλοποιεί το interface Iterator<Entry<K, V>> για να μπορούμε να διασχίζουμε
    // τον πίνακα κατακερματισμού χρησιμοποιώντας έξυπνες προτάσεις for
    private class HashIterator implements Iterator<Entry<K, V>> {

        private int i = 0; // μετρητής

        // επιστρέφει true αν υπάρχει επόμενο στοιχείο, διαφορετικά false
        @Override
        public boolean hasNext() {
            while (i < array.length) { // μέχρι να φτάσεις στο τέλος του πίνακα
                if (array[i] != null) { // αν το στοιχείο στην θέση i υπάρχει
                    return true; // επέστρεψε true
                }
                i++; // διαφορετικά αύξησε τον μετρητή i ώστε να δείχνει στην επόμενη θέση
            }
            return false; // όταν φτάσεις στο τέλος του πίνακα επέστρεψε false
        }

        // επιστρέφει το επόμενο στοιχείο στον πίνακα (αν υπάρχει)
        @Override
        public Entry<K, V> next() {
            if (!hasNext()) { // αν δεν υπάρχουν άλλα στοιχεία στον πίνακα
                throw new NoSuchElementException(); // εμφάνισε εξαίρεση
            }
            return array[i++]; // διαφορετικά επέστρεψε το στοιχείο και αύξησε τον δείκτη i
        }
    }
}
