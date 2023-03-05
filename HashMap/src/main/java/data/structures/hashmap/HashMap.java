package data.structures.hashmap;

import java.util.Scanner;
import java.util.StringTokenizer;
import data.structures.hashmap.Dictionary.Entry;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

// μετράει συχνότητες λέξεων σε αρχεία κειμένου
public class HashMap {

    // εκτελεί το πρόγραμμα
    public static void main(String args[]) {

        ArrayList<String> texts = new ArrayList<>(); // πίνακας που κρατάει τα txt files
        int sign = 0; // σημαία

        // για κάθε όρισμα γραμμής εντολών
        for (String arg : args) {
            File file = new File(arg); // δημιούργησε ένα αρχείο
            if (getFileExtension(file).equals(".txt")) { // αν το όρισμα είναι αρχείο κειμένου
                texts.add(arg); // πρόσθεσέ το στον πίνακα texts
            }
            else { // διαφορετικά εμφάνισε τα αρχεία ή τους καταλόγους που θα αγνοήσεις
                if (sign == 1) {
                    System.out.printf(" %s", arg);
                }
                else if (sign == 0 && !texts.isEmpty()) {
                    System.out.printf("%n>>> IGNORED: %s", arg);
                    sign = 1;
                }
            }
        }

        // έλεγχος εισόδου χρήστη
        if (args.length == 0) {
            System.err.printf("%n>>> Not enough arguments%n");
            System.exit(1);
        }
        else if (texts.isEmpty()) {
            System.err.printf("%n>>> No text files%n");
            System.exit(1);
        }
        else if (sign == 1) {
            System.out.println();
        }

        // δημιούργησε έναν πίνακα κατακερματισμού
        Dictionary<String, Integer> dict = new OpenAddressHashTable<>();

        // για κάθε αρχείο κειμένου στον πίνακα texts
        for (String text : texts) {
            // δημιούργησε ένα Scanner που να διαβάσει από το τρέχων αρχείο
            try ( Scanner scanner = new Scanner(new File(text))) {
                while (scanner.hasNext()) { // όσο δεν φτάνεις στο τέλος του αρχείου
                    String line = scanner.nextLine(); // αποθήκευσε την επόμενη γραμμή στην line
                    StringTokenizer st = new StringTokenizer(line); // χώρισε τη γραμμή σε λέξεις
                    while (st.hasMoreTokens()) { // όσο ακόμα υπάρχουν λέξεις
                        String word = st.nextToken(); // αποθήκευσε την επόμενη στην μεταβλητή word
                        Integer curFreq = dict.get(word); // ψάξε αν υπάρχει ήδη στον πίνακα
                        if (curFreq == null) { // αν η λέξη δεν υπάρχει
                            curFreq = 1; // θέσε την συχνότητά της σε 1
                        }
                        else { // διαφορετικά
                            curFreq++; // αύξησε την συχνότητά της
                        }
                        // πρόσθεσε την λέξη στον πίνακα - αν η λέξη
                        // υπάρχει ήδη απλά θα ενημερωθεί η συχνότητά της
                        dict.put(word, curFreq);
                    }
                }
            }
            catch (FileNotFoundException e) { // αν το αρχείο κειμένου δεν υπάρχει εμφάνισε μήνυμα
                System.err.printf("%n>>> Error: file %s not found%n", text);
                dict.clear(); // άδειασε τον πίνακα κατακερματισμού
                continue; // προχώρα στην επόμενη επανάληψη
            }

            // επικεφαλίδα
            System.out.printf("%nTITLE: %-15s%s%12s%22s%n%46s%13s%n",
                    text.substring(0, text.lastIndexOf(".")), " | ", "Word",
                    "| Appearances", "----------------------", "------------");

            // εκτύπωσε κάθε λέξη και τη συχνότητα που αυτή εμφανίζεται στο κείμενο
            for (Entry<String, Integer> e : dict) {
                System.out.printf("%25s%-20s | %s%n", "| ", e.getKey(), e.getValue());
            }

            dict.clear(); // άδειασε τον πίνακα κατακερματισμού
        }
    }

    // επιστρέφει την κατάληξη του αρχείου που περνάει σαν όρισμα
    private static String getFileExtension(File file) {
        String name = file.getName(); // πάρε το όνομα του αρχείου (αγνόησε το path)
        if (name.lastIndexOf(".") == -1) { // αν το file δεν είναι αρχείο
            return ""; // επέστρεψε κενή συμβολοσειρά
        }
        return name.substring(name.lastIndexOf(".")); // διαφορετικά επέστρεψε την κατάληξη
    }
}
