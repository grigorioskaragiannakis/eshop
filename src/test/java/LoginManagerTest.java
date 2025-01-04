import api.Auhentication.CryptographyUtils;
import api.Auhentication.LoginManager;
import api.Enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Δοκιμαστή Test κλάση που ελέγχει διάφορες λειτουργίες του {@link LoginManager}
 */
public class LoginManagerTest {

    private CryptographyUtils cryptographyUtils;

    @InjectMocks
    private LoginManager loginManager;

    private static final String USERS_CREDS = "users_creds.txt";

    @BeforeEach
    public void setup() {
        cryptographyUtils = new CryptographyUtils();
        loginManager = new LoginManager();
    }

    /**
     * Έλεγχος για το αν ένας χρήστης μπορεί να δημιουργηθεί με επιτυχία και επίσης ελέγχει αν το username είναι πιασμένο ή οχι
     * @throws Exception
     */
    @Test
    public void testCreateUser() throws Exception {
        // Dummy εγγραφές
        String name = "Vaggelis";
        String surname = "Moraitis";
        String username = "user2";
        String password = "123456789";
        String DELIMETER = ";";
        UserRole role = UserRole.ADMIN;

        // Hash του κωδικού
        String hashedPassword = cryptographyUtils.hash(password);

        Path path = Path.of(USERS_CREDS);

        // Προσθήκη του χρήστη
        loginManager.addUser(name, surname, username, password, role);

        BufferedReader reader = Files.newBufferedReader(path);
        String line = reader.readLine();
        String[] data = line.split(DELIMETER);

        // Έλεγχος αν προστέθηκαν τα στοιχεία σωστά
        assertEquals(name, data[0]);
        assertEquals(surname, data[1]);
        assertEquals(username, data[2]);
        assertEquals(hashedPassword, data[3]);
        assertEquals(role.name(), data[4]);

        reader.close(); // Κλείσιμο του αρχείου
    }

    /**
     * Έλεγχος για το εάν ο χρήστης έχει κάνει login με επιτυχία
     */
    @Test
    public void testIsLoggedIn() {
        String username = "user2";
        String password = "123456789";

        // Είσοδος
        loginManager.authenticateLogin(username, password);

        assertTrue(loginManager.isLoggedIn(), "User is not logged in!");
    }

    /**
     * Έλεγχος για το εάν ο χρήστης έχει κάνει logout με επιτυχία
     */
    @Test
    public void testIsLoggedOut() {
        String username = "user2";
        String password = "123456789";

        // Είσοδος
        loginManager.authenticateLogin(username, password);

        // Έξοδος
        loginManager.logout();

        assertFalse(loginManager.isLoggedIn(), "User shouldn't be logged in!");
    }
}
