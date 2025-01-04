package api.Entities;

import api.Enums.UserRole;

import java.util.List;
import java.util.Objects;

/** Κλάση που ορίζει έναν χρήστη.
 * Κάθε χρήστης περιέχει ένα username, password καθώς και τον ρόλο {@link UserRole} που έχει.
 *
 */
public class User {

    private String name;
    private String surname;
    private String username;
    private String password;
    private UserRole role;
    private List<Order> orderHistory;

    /**
     * * Constructor για τη κλάση User για αρχικοποίηση των τιμών
     *
     * @param name
     * @param surname
     * @param username
     * @param password
     * @param role
     * @param orderHistory
     */
    public User(String name, String surname, String username, String password, UserRole role, List<Order> orderHistory) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.role = Objects.isNull(role) ? UserRole.ADMIN : role; // Οι admins ειναι προκαθορισμένοι από το σύστημα
        this.orderHistory = orderHistory;
    }

    // Getters και Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", orderHistory=" + orderHistory +
                '}';
    }
}
