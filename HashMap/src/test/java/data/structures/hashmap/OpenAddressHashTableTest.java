package data.structures.hashmap;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

// δοκιμάζει τα όρια του πίνακα κατακερματισμού με 3 unit tests
public class OpenAddressHashTableTest {

    private final int SIZE = 10000; // αριθμός στοιχείων
    private Dictionary<Integer, Integer> dict; // πίνακας κατακερματισμού
    private List<Integer> values; // λίστα που κρατάει τους τυχαίους ακεραίους
    private Random rng; // για τη δημουργία τυχαίων ακεραίων

    // αρχικοποιεί τις μεταβλητές
    public OpenAddressHashTableTest() {
        dict = new OpenAddressHashTable<>();
        values = new ArrayList<>();
        rng = new Random();
    }

    // δοκιμάζει την εισαγωγή και την αναζήτηση
    @Test
    public void testPutAndGet() {
        // αρχικοποίησε την λίστα και τον πίνακα κατακερματισμού
        // η init εισάγει τα στοιχεία στον πίνακα κατακερματισμού
        init();

        for (Integer v : values) { // για κάθε ακέραιο στην λίστα values
            // βεβαιώσου πως η αναζήτηση στον πίνακα κατακερματισμού επιστρέφει το σωστό value
            assertTrue(dict.get(v) == v + 1);
        }

        // άδειασε την λίστα και τον πίνακα κατακερματισμού
        clear();
    }

    // δοκιμάζει την διαγραφή
    @Test
    public void testRemoveAndContains() {
        init(); // αρχικοποίησε την λίστα και τον πίνακα κατακερματισμού

        for (int i = 0; i < values.size(); i++) { // για κάθε ακέραιο στην λίστα values
            assertTrue(dict.contains(values.get(i))); // βεβαιώσου πως το στοιχείο υπάρχει στον πίνακα κατακερματισμού
            assertTrue(dict.remove(values.get(i)) == values.get(i) + 1); // και η διαγραφή του επιστρέφει το σωστό value

            // διαγράφει όλους τους ακέραιους που επιστρέφει η get από την λίστα
            while (i < values.size() && values.contains(values.get(i))) {
                values.remove(values.get(i));
            }
        }
        clear(); // άδειασε την λίστα και τον πίνακα κατακερματισμού
    }

    // δοκιμάζει τις συναρτήσεις clear και isEmpty
    @Test
    public void testClearAndIsEmpty() {
        init(); // αρχικοποίησε την λίστα και τον πίνακα κατακερματισμού
        assertTrue(!dict.isEmpty()); // βεβαιώσου πως ο πίνακας δεν είναι άδειος
        clear(); // άδειασε την λίστα και τον πίνακα κατακερματισμού
        assertTrue(dict.isEmpty()); // βεβαιώσου πως ο πίνακας είναι άδειος
    }

    // γεμίζει την λίστα values με τυχαίους ακεραίους και τους εισάγει στον πίνακα κατακερματισμού
    private void init() {
        for (int i = 0; i < SIZE; i++) { // για SIZE επαναλήψεις
            int n = rng.nextInt(1000); // επέλεξε έναν τυχαίο ακέραιο μεταξύ του 0 και 999
            values.add(n); // πρόσθεσε τον ακέραιο στην λίστα
            dict.put(n, n + 1); // πρόσθεσε τον ακέραιο σαν στοιχείο στον πίνακα κατακερματισμού

            // σημείωση: η λίστα μπορεί να περιέχει τον ίδιο ακέραιο πολλές φορές
            // ενώ ο πίνακας κατακερματισμού θα τον αποθηκεύσει μόνο μία φορά
            // επομένως το μέγεθος του πίνακα μπορεί να είναι μικρότερο από το μέγεθος της λίστας
        }
    }

    // αδείαζει την λίστα και τον πίνακα κατακερματισμού
    private void clear() {
        dict.clear(); // άδειασε τον πίνακα
        values.clear(); // άδειασε την λίστα
    }
}
