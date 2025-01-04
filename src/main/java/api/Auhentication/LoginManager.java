package api.Auhentication;

import api.Entities.User;
import api.Enums.UserRole;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Κλάση η οποία διαχειρίζεται τη διαδικασία του login καθώς και της εγγραφής ενός χρήστη
 */
public class LoginManager {

    // Το αρχείο που θα κρατάει τα δεδομένα των χρηστών
    private final String USER_CREDS = "users_creds.txt";
    private final String DELIMETER = ";";
    private CryptographyUtils cryptographyUtils;
    private User loggedInUser;

    /**
     * Constructor για αρχικοποίηση των απαραίτητων πεδίων.
     */
    public LoginManager() {
        this.cryptographyUtils = new CryptographyUtils();
        this.loggedInUser = null;
    }

    /**
     * Προσθήκη χρήστη στα αρχεία του συστήματος.
     * @param username
     * @param password
     * @param role
     * @return true αν επιτυχής προσθήκη διαφορετικά false;
     */
    public boolean addUser(String name, String surname, String username, String password, UserRole role) {
        String hashedPassword = cryptographyUtils.hash(password);

        // Έλεγχος για το αν ο χρήστης έχει δώσει όλα τα απαραίτητα πεδία
        if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return false;
        }

        if (usernameAlreadyUsed(username)) {
            return false;
        }

        try {
            FileWriter fileWriter = new FileWriter(USER_CREDS, true);
            fileWriter.write(name + DELIMETER + surname + DELIMETER + username + DELIMETER + hashedPassword + DELIMETER + role);
            fileWriter.write("\n");
            fileWriter.close();
        }
        catch (Exception e){
            System.out.println("Λάθος κατα τη διαδικασία προσθήκης χρήστη! " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Ελέγχει αν ο χρήστης υπάρχει στα αρχεία και τον αυθεντικοποιεί ελέγχοντας username & κωδικό.
     * Αν αυθεντικοποιήσει τον χρήστη τότε επιστρέφει τον logged in χρήστη.
     * @param username
     * @param password
     * @return αντικείμενο τύπου {@link User}
     */
    public User authenticateLogin(String username, String password) {
        try {
            // Διάβασμα αρχείου με στοιχεία χρηστών
            BufferedReader bufferedReader = new BufferedReader(new FileReader(USER_CREDS));

            String line;

            while(Objects.nonNull(line = bufferedReader.readLine())) {
                String[] data;

                // Χωρίζουμε τις γραμμές με delimer :
                data = line.split(DELIMETER);

                // Αν έχει δώσει σωστά στοιχεία τότε επέστρεψε τον χρήστη
                if(data[2].equals(username) && cryptographyUtils.checkPassword(password, data[3])) {
                    loggedInUser = new User(data[0], data[1], username, password, UserRole.valueOf(data[4]), new ArrayList<>());
                    return loggedInUser;
                }
            }
        }
        catch (Exception e){
            System.out.println("Λάθος κατα τη διαδικασία login! " + e.getMessage());
            return null;
        }
        return loggedInUser;
    }

    /**
     * Κάνει logout τον χρήστη αναθέτοντας το αντικείμενο που κρατάει τον ενεργό χρήστη σε null
     * @return true αν κάνει με επιτυχία logout, false σε διαφορετική περίπτωση
     */
    public boolean logout() {
        if(Objects.nonNull(loggedInUser))
            loggedInUser = null;
        else
            return false;

        return true;
    }

    /**
     * Ελέγχει αν είναι logged in ο χρήστης
     * @return true αν είναι logged in, false σε διαφορετική περίπτωση
     */
    public boolean isLoggedIn() {
        return Objects.nonNull(loggedInUser);
    }

    /**
     * Ελέγχει αν χρησιμοποιείται το username ήδη.
     * @param username το username προς έλεγχο
     * @return true αν χρησιμοποοιείται ήδη, διαφορετικά false
     */
    public boolean usernameAlreadyUsed(String username) {
        File file = new File(USER_CREDS);

        if (!file.exists()) {
            return true;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(USER_CREDS))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(DELIMETER);
                if (data[2].equals(username)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
