package api.Auhentication;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * Κλάση που περιέχει λειτουργίες για κρυπτογράφηση και αποκρυπτογράφηση ενός κωδικού με χρήση "SHA-256".
 */
public class CryptographyUtils {

    /**
     * Παράγει το hash ενός κωδικού με χρήση "SHA-256"
     * @param password
     * @return Tο hash του κωδικού που πήρε σαν είσοδο
     */
    public String hash(String password) {
        MessageDigest digest = null;
        try{
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch(Exception e) {
            System.out.println("Λάθος στη διαδικασία hasing!" + e.getMessage());
            return null;
        }

        byte[] encodedHash = digest.digest(password.getBytes());

        return Base64.getEncoder().encodeToString(encodedHash);
    }

    /**
     * Παίρνει σαν παράμετρο τον κωδικό που εισάγει ο χρήστης και τον hashed κωδικό που υπάρχει στο αρχείο και ελέγχει εάν είναι ίδια.
     * @param password
     * @param hashedPassword
     * @return true/false ανάλογα με το αν ισχύει η συνθήκη
     */
    public boolean checkPassword(String password, String hashedPassword) {

        // Επιστρέφουμε true ή false
        return hash(password).equals(hashedPassword);
    }
}
