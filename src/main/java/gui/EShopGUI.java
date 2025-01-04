package gui;

// Εισγαωγή απραίτητων βιβλιοθηκών
import api.Auhentication.LoginManager;
import api.Entities.*;
import api.Enums.UserRole;
import api.Services.CartService;
import api.Services.InitializationService;
import api.Services.OrderService;
import api.Services.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Κλάση που περιέχει όλα τα γραφικά της εφαρμογής
 */
public class EShopGUI {

    // Απαιραίτητες μεταβλητές δήλωση
    private LoginManager loginManager;
    private ProductService productService;
    private CartService cartService;
    private OrderService orderService;
    private User user;
    JComboBox<String> productDropdown = new JComboBox<>();
    JPanel cartPanel;
    private JLabel totalCostLabel;


    /**
     * Constructor που αρχικοποιεί τα απαραίτητα στοιχεία του GUI και διαβάζει τα data από τα αρχεία που έχουμε αποθηκευμένα για να αρχικοποιήσει το σύστημά μας.
     */
    public EShopGUI() {
        this.loginManager = new LoginManager();
        this.productService = new ProductService();
        this.cartService = new CartService();
        this.orderService = new OrderService(cartService);
        this.user = null;
        //Διάβασμα από αρχεία
        InitializationService.initializeCategoriesFromFile(productService, "categoriessubcategories.txt");
        InitializationService.initializeProductsFromFile(productService, "products.txt");
        InitializationService.initializeOrdersFromFile(orderService, "orders.txt");
    }

    /**
     * Συνάρτηση που ξεκινάει το πρόγραμμα του EShop
     */
    public void start() {

        // Δίνουμε τον τίτλο και τα βασικά Panels
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("EShop");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel(new CardLayout());
            frame.setContentPane(mainPanel);

            JPanel signInPanel = createSignInPanel(mainPanel, frame);
            JPanel registerPanel = createRegisterPanel(mainPanel);

            mainPanel.add(signInPanel, "Είσοδος");
            mainPanel.add(registerPanel, "Εγγραφή");

            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            // Όταν κλείνουμε το πρόγραμμα αποθηκέυουμε τα data
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    InitializationService.saveProductsToFile(productService.getAllProducts(), "products.txt");
                    InitializationService.saveOrdersToFile(orderService.getAllOrders(), "orders.txt");
                    System.exit(0);
                }
            });
            frame.setVisible(true);
        });
    }

    /**
     * Επιστρέφει το Jpanel για το sign in
     * @param mainPanel
     * @param frame
     * @return JPanel του sign-in
     */
    private JPanel createSignInPanel(JPanel mainPanel, JFrame frame) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Το padding για τα στοιχεία
        gbc.anchor = GridBagConstraints.WEST;

        // Labels για Username & Password
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        // Κουμπιά
        JButton signInButton = new JButton("Είσοδος");
        JButton registerButton = new JButton("Εγγραφή");

        // Label για το μήνυμα λάθους (κόκκινο)
        JLabel errorLabel = new JLabel("", JLabel.CENTER);
        errorLabel.setForeground(Color.RED);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(signInButton, gbc);

        gbc.gridy = 3;
        panel.add(registerButton, gbc);

        gbc.gridy = 4;
        panel.add(errorLabel, gbc);

        // ActionListener για το κουμπί του logi-in
        signInButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            user = loginManager.authenticateLogin(username, password);

            if (user != null) {
                System.out.println("Επιτυχία: " + user.getUsername());
                setPanelsBasedOnRole(mainPanel, frame, user);
            } else {
                System.out.println("Αποτυχία εισόδου: Λάθος username/ Κωδικός");
                errorLabel.setText("Λάθος username/ κωδικός");
            }

            mainPanel.revalidate();
            mainPanel.repaint();
        });

        // ActionListener για το κουμπί εγραφής νέου χρήστη
        registerButton.addActionListener(e -> {
            CardLayout layout = (CardLayout) mainPanel.getLayout();
            layout.show(mainPanel, "Εγγραφή");
        });

        return panel;
    }

    /**
     * Επιστρέφει το Jpanel για την εγγραφή νέου πελάτη.
     * @param mainPanel
     * @return JPanel εγγραφής πελάτη
     */
    private JPanel createRegisterPanel(JPanel mainPanel) {
        JPanel panel = new JPanel(new GridLayout(7, 1, 5, 5));
        JLabel nameLabel = new JLabel("Όνομα:");
        JTextField nameField = new JTextField();

        JLabel surnameLabel = new JLabel("Επίθετο:");
        JTextField surnameField = new JTextField();

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Κωδικός:");
        JPasswordField passwordField = new JPasswordField();

        JButton registerButton = new JButton("Εγγραφή");
        JButton backButton = new JButton("Είσοδος");

        JLabel errorLabel = new JLabel("", JLabel.CENTER);
        errorLabel.setForeground(Color.RED);

        registerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Πρέπει να συμπληρώσετε όλα τα πεδία!");
                return;
            }

            if (loginManager.usernameAlreadyUsed(username)) {
                errorLabel.setText("Το username υπάρχει ήδη");
                return;
            }

            // Εγγραφή χρήστη
            boolean hasRegistered = loginManager.addUser(name, surname, username, password, UserRole.USER);
            if (hasRegistered) {
                JOptionPane.showMessageDialog(null, "Επιτυχής εγγραφή!", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
                CardLayout layout = (CardLayout) mainPanel.getLayout();
                layout.show(mainPanel, "Είσοδος");
            }
        });

        backButton.addActionListener(e -> {
            CardLayout layout = (CardLayout) mainPanel.getLayout();
            layout.show(mainPanel, "Είσοδος");
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(surnameLabel);
        panel.add(surnameField);
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(registerButton);
        panel.add(backButton);
        panel.add(errorLabel);

        return panel;
    }

    /**
     * Επιστρέφει το panel για τον administrator
     * @param mainPanel
     * @param productDropdown
     * @return JPanel για τον admin
     */
    private JPanel createAdminPanel(JPanel mainPanel, JComboBox<String> productDropdown) {
        JPanel panel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel productRegistrationTab = createProductAddTab(mainPanel);
        tabbedPane.addTab("Καταχώρηση Προϊόντος", productRegistrationTab);

        JPanel editProductTab = createProductDetailsTab(mainPanel, null);
        tabbedPane.addTab("Επεξεργασία Προϊόντος", editProductTab);

        JPanel searchPanel = createSearchPanel(mainPanel, productDropdown);
        tabbedPane.addTab("Αναζήτση Προϊόντος", searchPanel);

        JPanel statisticsTab = createStatisticsTab();
        tabbedPane.addTab("Στατιστικά", statisticsTab);

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Επιστρέφει το panel για τον πελάτη
     * @param mainPanel
     * @param productDropdown
     * @return JPanel για τον πελάτη
     */
    private JPanel createUserPanel(JPanel mainPanel, JComboBox<String> productDropdown) {

        this.cartService.createCartForUser(user.getUsername());

        JPanel panel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel productDetailsTab = createProductDetailsTab(mainPanel, null);
        tabbedPane.addTab("Προβολή προϊόντος", productDetailsTab);

        JPanel searchPanel = createSearchPanel(mainPanel, productDropdown);
        tabbedPane.addTab("Αναζήτση Προϊόντος", searchPanel);

        cartPanel = createCartPanel(mainPanel);
        tabbedPane.addTab("Καλάθι", cartPanel);

        JPanel orderHistoryTab = createOrderHistoryTab();
        tabbedPane.addTab("Ιστορικό Παραγγελιών", orderHistoryTab);

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Επιστρέφει το tab για την καταχώρηση νέου προι¨οντος
     * @param mainPanel
     * @return JPanel του tab νέου προιόντος
     */
    private JPanel createProductAddTab(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Καταχώρηση Προϊόντος", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        JLabel productTitleLabel = new JLabel("Τίτλος:");
        JTextField productTitleField = new JTextField();

        JLabel descriptionLabel = new JLabel("Περιγραφή:");
        JTextArea descriptionField = new JTextArea(3, 20);

        JLabel categoryLabel = new JLabel("Κατηγορία:");
        JComboBox<String> categoryComboBox = new JComboBox<>();

        JLabel subcategoryLabel = new JLabel("Υποκατηγορία:");
        JComboBox<String> subcategoryComboBox = new JComboBox<>();
        JComboBox<String> quantityUnitComboBox = new JComboBox<>(new String[]{"kg", "τεμάχια"});


        List<Category> categories = productService.getCategories();

        // Προσθέτει στο combobox τις κατηγορίες
        for (Category category : categories) {
            categoryComboBox.addItem(category.getName());
        }

        categoryComboBox.addActionListener(e -> {
            String selectedCategoryName = (String) categoryComboBox.getSelectedItem();
            subcategoryComboBox.removeAllItems();

            if (selectedCategoryName != null) {
                for (Category category : categories) {
                    if (category.getName().equals(selectedCategoryName)) {
                        for (SubCategory subcategory : category.getSubcategories()) {
                            subcategoryComboBox.addItem(subcategory.getName());
                        }
                        break;
                    }
                }
            }
        });

        JLabel priceLabel = new JLabel("Τιμή:");
        JTextField priceField = new JTextField();

        JLabel stockLabel = new JLabel("Διαθέσιμα:");
        JTextField stockField = new JTextField();

        JLabel stockTypeLabel = new JLabel("Μονάδα:");
        JComboBox<String> type = new JComboBox<>(new String[]{"Τεμάχια", "Κιλά"});

        JButton saveButton = new JButton("Αποθήκευση");
        JButton logoutButton = new JButton("Αποσύνδεση");

        saveButton.addActionListener(e -> {
            String title = productTitleField.getText().trim();
            String description = descriptionField.getText().trim();
            String category = (String) categoryComboBox.getSelectedItem();
            String subcategory = (String) subcategoryComboBox.getSelectedItem();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();
            String quantityUnit = (String) quantityUnitComboBox.getSelectedItem();

            Map<String, String> productDetails = new HashMap<>();
            productDetails.put("Τίτλος", title);
            productDetails.put("Περιγραφή", description);
            productDetails.put("Κατηγορία", category);
            productDetails.put("Υποκατηγορία", subcategory);
            productDetails.put("Τιμή", priceText + "€");
            productDetails.put("Ποσότητα", stockText + " " + quantityUnit);

            try {
                productService.addProductByDetails(productDetails);

                JOptionPane.showMessageDialog(null, "Το προϊόν καταχωρήθηκε επιτυχώς!", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);

                productTitleField.setText("");
                descriptionField.setText("");
                categoryComboBox.setSelectedIndex(0);
                subcategoryComboBox.removeAllItems();
                priceField.setText("");
                stockField.setText("");
                quantityUnitComboBox.setSelectedIndex(0);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Σφάλμα!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        logoutButton.addActionListener(e -> {
            loginManager.logout();
            CardLayout layout = (CardLayout) mainPanel.getLayout();
            layout.show(mainPanel, "Είσοδος");
            mainPanel.revalidate();
            mainPanel.repaint();
        });


        formPanel.add(productTitleLabel);
        formPanel.add(productTitleField);
        formPanel.add(descriptionLabel);
        formPanel.add(new JScrollPane(descriptionField));
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(subcategoryLabel);
        formPanel.add(subcategoryComboBox);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(stockLabel);
        formPanel.add(stockField);
        formPanel.add(stockTypeLabel);
        formPanel.add(type);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Επιστρέφει το Tab για τις λεπτομέρειες των προϊοντων
     * @param mainPanel
     * @param item
     * @return JPanel για τις λεπτομέρειες προι¨οντων
     */
    private JPanel createProductDetailsTab(JPanel mainPanel, Product item) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Επεξεργασία Προϊόντος", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        List<Product> products = productService.getAllProducts();
        System.out.println("Loaded products: " + products.size());
        for (Product product : products) {
            System.out.println("Adding product: " + product.getTitle());
            productDropdown.addItem(product.getTitle());
        }

        JPanel productSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel productLabel = new JLabel("Επιλέξτε Προϊόν:");

        // Επαναφέρει τη λίστα με όλα τα προϊόντα
        JButton resetButton = new JButton("Επαναφορά Λίστας");


        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        JLabel productTitleLabel = new JLabel("Τίτλος:");
        JTextField productTitleField = new JTextField();

        JLabel descriptionLabel = new JLabel("Περιγραφή:");
        JTextArea descriptionField = new JTextArea(3, 20);

        JLabel categoryLabel = new JLabel("Κατηγορία:");
        JComboBox<String> categoryComboBox = new JComboBox<>();

        JLabel subcategoryLabel = new JLabel("Υποκατηγορία:");
        JComboBox<String> subcategoryComboBox = new JComboBox<>();

        List<Category> categories = productService.getCategories();
        for (Category category : categories) {
            categoryComboBox.addItem(category.getName());
        }

        resetButton.addActionListener(e -> {
            productDropdown.removeAllItems();
            List<Product> allProducts = productService.getAllProducts();
            for (Product product : allProducts) {
                productDropdown.addItem(product.getTitle());
            }
        });

        // ActionListener για το combobox
        categoryComboBox.addActionListener(e -> {
            String selectedCategoryName = (String) categoryComboBox.getSelectedItem();
            subcategoryComboBox.removeAllItems();

            if (selectedCategoryName != null) {
                for (Category category : categories) {
                    if (category.getName().equals(selectedCategoryName)) {
                        for (SubCategory subcategory : category.getSubcategories()) {
                            subcategoryComboBox.addItem(subcategory.getName());
                        }
                        break;
                    }
                }
            }
        });

        JLabel priceLabel = new JLabel("Τιμή:");
        JTextField priceField = new JTextField();

        JLabel stockLabel = new JLabel("Διαθέσιμα:");
        JTextField stockField = new JTextField();

        JComboBox<String> quantityUnitComboBox = new JComboBox<>(new String[]{"kg", "τεμάχια"});
        JLabel quantityLabel = new JLabel("Ποσότητα:");
        JTextField quantityCartField = new JTextField();
        quantityCartField.setPreferredSize(new Dimension(100, 30));

        productDropdown.addActionListener(e -> {
            String selectedProductTitle = (String) productDropdown.getSelectedItem();
            Product selectedProduct = productService.getProductByName(selectedProductTitle);
            if (selectedProduct != null) {
                productTitleField.setText(selectedProduct.getTitle());
                descriptionField.setText(selectedProduct.getDescription());
                priceField.setText(String.valueOf(selectedProduct.getPrice()));
                stockField.setText(String.valueOf(selectedProduct.getQuantity()));
                categoryComboBox.setSelectedItem(selectedProduct.getCategory().getName());
                subcategoryComboBox.removeAllItems();
                for (SubCategory subcategory : selectedProduct.getCategory().getSubcategories()) {
                    subcategoryComboBox.addItem(subcategory.getName());
                }
            }
        });

        JButton saveButton = new JButton("Αποθήκευση");
        JButton logoutButton = new JButton("Αποσύνδεση");

        logoutButton.addActionListener(e -> {
            loginManager.logout();
            CardLayout layout = (CardLayout) mainPanel.getLayout();
            layout.show(mainPanel, "Είσοδος");
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        saveButton.addActionListener(e -> {
            String title = productTitleField.getText().trim();
            String description = descriptionField.getText().trim();
            String category = (String) categoryComboBox.getSelectedItem();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();

            try {
                BigDecimal price = new BigDecimal(priceText.replace(",", "."));
                double stock = Double.parseDouble(stockText);

                Product selectedProduct = productService.getProductByName((String) productDropdown.getSelectedItem());
                if (selectedProduct != null) {
                    selectedProduct.setTitle(title);
                    selectedProduct.setDescription(description);
                    selectedProduct.setPrice(price);
                    selectedProduct.setQuantity(stock);
                    selectedProduct.setCategory(productService.getCategoryByName(category));
                    JOptionPane.showMessageDialog(null, "Το προϊόν ενημερώθηκε επιτυχώς!", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Σφάλμα!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JButton addToCartButton = new JButton("Προσθήκη στο Καλαθι");
        addToCartButton.addActionListener(addEvent -> {
            String selectedProductTitle = (String) productDropdown.getSelectedItem();
            Product selectedProduct = productService.getProductByName(selectedProductTitle);

            String quantityStr = quantityCartField.getText().trim();
            if (selectedProduct != null && !quantityStr.isEmpty()) {
                try {
                    Number quantity = selectedProduct instanceof PiecesProduct ? Integer.parseInt(quantityStr) : Double.parseDouble(quantityStr);

                    boolean success = cartService.addItemToCart(user.getUsername(), selectedProduct, quantity);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Το προϊόν προστέθηκε στο καλάθι!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Παρακαλώ εισάγετε έγκυρη ποσότητα!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Παρακαλώ εισάγετε έγκυρη ποσότητα!");
                    System.out.println("Invalid quantity input: " + quantityStr);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Παρακαλώ επιλέξτε προϊόν και εισάγετε ποσότητα!");
                System.out.println("Invalid product or quantity");
            }
        });

        // Ανάλογα με το εάν ο χρήστης είναι administrator ή όχι εμφανίζουμε ανάλογα κουμπιά
        JPanel buttonPanel = new JPanel();
        if (user.getRole() == UserRole.ADMIN) {
            buttonPanel.add(saveButton);
        } else {
            buttonPanel.add(addToCartButton);
            buttonPanel.add(quantityLabel);
            buttonPanel.add(quantityCartField);
            buttonPanel.add(logoutButton);
        }

        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        productTitleField.setEditable(isAdmin);
        descriptionField.setEditable(isAdmin);
        priceField.setEditable(isAdmin);
        stockField.setEditable(isAdmin);
        categoryComboBox.setEnabled(isAdmin);
        subcategoryComboBox.setEnabled(isAdmin);
        quantityUnitComboBox.setEditable(isAdmin);

        addToCartButton.setVisible(!isAdmin);

        productSelectionPanel.add(productLabel);
        productSelectionPanel.add(productDropdown);
        productSelectionPanel.add(resetButton);

        panel.add(productSelectionPanel, BorderLayout.NORTH);

        formPanel.add(productTitleLabel);
        formPanel.add(productTitleField);
        formPanel.add(descriptionLabel);
        formPanel.add(new JScrollPane(descriptionField));
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(subcategoryLabel);
        formPanel.add(subcategoryComboBox);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(stockLabel);
        formPanel.add(stockField);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Επιστρέφει το JPanel για την αναζήτηση προϊόντος
     * @param mainPanel
     * @param productDropdown
     * @return JPanel αναζήτης προϊόντος
     */
    private JPanel createSearchPanel(JPanel mainPanel, JComboBox<String> productDropdown) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel searchTitleLabel = new JLabel("Αναζήτηση Προϊόντων", JLabel.CENTER);
        searchTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel searchFormPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel titleLabel = new JLabel("Τίτλος:");
        JTextField titleField = new JTextField();

        JLabel categoryLabel = new JLabel("Κατηγορία:");
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem("-");

        JLabel subcategoryLabel = new JLabel("Υποκατηγορία:");
        JComboBox<String> subcategoryComboBox = new JComboBox<>();
        subcategoryComboBox.addItem("-");

        List<Category> categories = productService.getCategories();
        for (Category category : categories) {
            categoryComboBox.addItem(category.getName());
        }

        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            subcategoryComboBox.removeAllItems();
            subcategoryComboBox.addItem("Select Subcategory");

            if (selectedCategory != null && !selectedCategory.equals("Select Category")) {
                for (Category category : categories) {
                    if (category.getName().equals(selectedCategory)) {
                        for (SubCategory subcategory : category.getSubcategories()) {
                            subcategoryComboBox.addItem(subcategory.getName());
                        }
                        break;
                    }
                }
            }
        });

        JButton searchButton = new JButton("Αναζήτηση");

        searchButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            String selectedSubcategory = (String) subcategoryComboBox.getSelectedItem();

            List<Product> searchResults;

            if (selectedSubcategory != null && !selectedSubcategory.equals("-")) {
                searchResults = productService.getProductsByTitleAndSubcategory(title, selectedSubcategory);
            }
            else if (selectedCategory != null && !selectedCategory.equals("-")) {
                searchResults = productService.getProductsByTitleAndCategory(title, selectedCategory);
            }
            else if (!title.isEmpty()) {
                searchResults = productService.getProductsByTitle(title);
            }
            else
                searchResults = productService.getAllProducts();

            if (!searchResults.isEmpty()) {
                productDropdown.removeAllItems();
                for (Product product : searchResults) {
                    productDropdown.addItem(product.getTitle());
                }

                JPanel panelBasedOnRole = (JPanel) mainPanel.getComponent(2);

                CardLayout layout = (CardLayout) mainPanel.getLayout();
                layout.show(mainPanel, "Διαχειριστής");

                JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) panelBasedOnRole.getLayout()).getLayoutComponent(BorderLayout.CENTER);

                // Έλεγχος για το εάν είναι admin ο χρήστης ή οχι
                if(user.getRole() == UserRole.ADMIN)
                    tabbedPane.setSelectedIndex(1);
                else
                    tabbedPane.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(null, "Δεν βρέθηκαν προϊόντα!", "Αναζήτηση", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        searchFormPanel.add(titleLabel);
        searchFormPanel.add(titleField);
        searchFormPanel.add(categoryLabel);
        searchFormPanel.add(categoryComboBox);
        searchFormPanel.add(subcategoryLabel);
        searchFormPanel.add(subcategoryComboBox);

        panel.add(searchTitleLabel, BorderLayout.NORTH);
        panel.add(searchFormPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(searchButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Επιστρέφει το JPanel για το καλάθι
     * @param mainPanel
     * @return JPanel Καλαθιού
     */
    private JPanel createCartPanel(JPanel mainPanel) {
        // Αν υπάρχει ήδη το επιστρέφουμε
        if (cartPanel != null) {
            return cartPanel;
        }

        cartPanel = new JPanel(new BorderLayout());

        JLabel cartTitleLabel = new JLabel("Το Καλάθι Μου", JLabel.CENTER);
        cartTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        String[] columnNames = {"Όνομα Προϊόντος", "Ποσότητα", "Τιμή ανά τεμάχιο/κιλό", "Συνολικό Κόστος", "Ενέργειες"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 4;
            }
        };

        JTable cartTable = new JTable(tableModel);

        cartTable.getColumnModel().getColumn(1).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel();
            if (value != null) {
                label.setText(value.toString());
            } else {
                label.setText("");
            }
            label.setHorizontalAlignment(SwingConstants.CENTER);

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setOpaque(true);
            } else {
                label.setBackground(table.getBackground());
                label.setOpaque(false);
            }

            return label;
        });

        cartTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JTextField editor = new JTextField(value.toString());
                editor.setHorizontalAlignment(SwingConstants.CENTER);
                editor.addActionListener(e -> {
                    try {
                        double newQuantity = Double.parseDouble(editor.getText());
                        if (newQuantity <= 0) {
                            JOptionPane.showMessageDialog(cartPanel, "Η ποσότητα πρέπει να είναι μεγαλύτερη του μηδενός!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                            editor.setText(value.toString());
                        } else {
                            Product product = cartService.getCartForUser(user.getUsername()).getItems().keySet().toArray(new Product[0])[row];
                            if (newQuantity > product.getQuantity().doubleValue()) {
                                JOptionPane.showMessageDialog(cartPanel, "Η ποσότητα που ζητήσατε δεν είναι διαθέσιμη!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                                editor.setText(value.toString());
                            } else {
                                cartService.getCartForUser(user.getUsername()).getItems().put(product, newQuantity);
                                updateCartTable(cartService.getCartForUser(user.getUsername()), tableModel, totalCostLabel);
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(cartPanel, "Παρακαλώ εισάγετε έγκυρη ποσότητα!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                        editor.setText(value.toString());
                    }
                });
                return editor;
            }
        });

        cartTable.getColumnModel().getColumn(4).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton("Αφαίρεση");
            if (isSelected) {
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setBackground(UIManager.getColor("Button.background"));
            }
            return button;
        });

        cartTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JButton button = new JButton("Αφαίρεση");

            {
                button.addActionListener(e -> {
                    int selectedRow = cartTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String productName = (String) tableModel.getValueAt(selectedRow, 0);
                        Product product = productService.getProductByName(productName);

                        cartService.removeItemFromCart(user.getUsername(), productName, product.getQuantity());

                        tableModel.removeRow(selectedRow);
                        updateCartTable(cartService.getCartForUser(user.getUsername()), tableModel, totalCostLabel);
                    }
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                return button;
            }
        });

        JScrollPane scrollPane = new JScrollPane(cartTable);

        totalCostLabel = new JLabel("Συνολικό Κόστος: €0.00", JLabel.RIGHT);
        totalCostLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton completeOrderButton = new JButton("Ολοκλήρωση Παραγγελίας");
        completeOrderButton.addActionListener(e -> {
            Cart userCart = cartService.getCartForUser(user.getUsername());
            if (!userCart.getItems().isEmpty()) {
                for (Map.Entry<Product, Number> entry : userCart.getItems().entrySet()) {
                    Product product = entry.getKey();
                    Number quantity = entry.getValue();
                    product.setQuantity(product.getQuantity().doubleValue() - quantity.doubleValue());
                }

                JOptionPane.showMessageDialog(cartPanel, "Η παραγγελία ολοκληρώθηκε με επιτυχία!", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
                orderService.placeOrder(user.getUsername());

                userCart.getItems().clear();
                updateCartTable(userCart, tableModel, totalCostLabel);

                totalCostLabel.setText("Συνολικό Κόστος: €0.00");
            } else {
                JOptionPane.showMessageDialog(cartPanel, "Το καλάθι είναι άδειο!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(completeOrderButton);

        cartPanel.add(cartTitleLabel, BorderLayout.NORTH);
        cartPanel.add(scrollPane, BorderLayout.CENTER);
        cartPanel.add(totalCostLabel, BorderLayout.SOUTH);
        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        cartPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                Cart userCart = cartService.getCartForUser(user.getUsername());
                updateCartTable(userCart, tableModel, totalCostLabel);
            }
        });

        return cartPanel;
    }

    /**
     * Καλείτε για να ανενω΄σει το view του καλαθιού μετά απο κάποια ενέργεια
     * @param userCart
     * @param tableModel
     * @param totalCostLabel
     */
    private void updateCartTable(Cart userCart, DefaultTableModel tableModel, JLabel totalCostLabel) {
        tableModel.setRowCount(0);

        double totalCost = 0.0;

        for (Map.Entry<Product, Number> entry : userCart.getItems().entrySet()) {
            Product product = entry.getKey();
            Number quantity = entry.getValue();

            double productCost = product.getPrice().doubleValue() * quantity.doubleValue();
            totalCost += productCost;

            tableModel.addRow(new Object[]{
                    product.getTitle(),
                    quantity.toString(),
                    String.format("€%.2f", product.getPrice()),
                    String.format("€%.2f", productCost),
                    "Αφαίρεση"
            });
        }

        totalCostLabel.setText("Συνολικό Κόστος: €" + String.format("%.2f", totalCost));
    }

    /**
     * Επσιτρέφει το tab για τα στατιστικά
     * @return JPanel στατιστικών
     */
    private JPanel createStatisticsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Στατιστικά Προϊόντων", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel unavailableProductsPanel = new JPanel();
        unavailableProductsPanel.setLayout(new BoxLayout(unavailableProductsPanel, BoxLayout.Y_AXIS));
        unavailableProductsPanel.setBorder(BorderFactory.createTitledBorder("Μη Διαθέσιμα Προϊόντα"));

        List<Product> unavailableProducts = productService.getUnavailableProducts();
        for (Product product : unavailableProducts) {
            JLabel productLabel = new JLabel("* " + product.getTitle());
            unavailableProductsPanel.add(productLabel);
        }

        JPanel mostOrderedProductsPanel = new JPanel();
        mostOrderedProductsPanel.setLayout(new BoxLayout(mostOrderedProductsPanel, BoxLayout.Y_AXIS));
        mostOrderedProductsPanel.setBorder(BorderFactory.createTitledBorder("Προϊόντα σε Περισσότερες Παραγγελίες"));

        List<Product> mostOrderedProducts = orderService.getMostOrderedProducts();
        for (Product product : mostOrderedProducts) {
            JLabel productLabel = new JLabel("* " + product.getTitle());
            mostOrderedProductsPanel.add(productLabel);
        }

        statsPanel.add(unavailableProductsPanel);
        statsPanel.add(mostOrderedProductsPanel);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }


    /**
     * Επσιτρέφει το tab για το ιστορικό παραγγελιών
     * @return JPanel ιστορικό παραγγελιών
     */
    private JPanel createOrderHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Ιστορικό Παραγγελιών", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(historyPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Listener που για να ανανεώνει το view όταν μπούμε στο tab
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                historyPanel.removeAll();

                List<Order> updatedOrderHistory = orderService.getOrdersByUser(user.getUsername());

                if (updatedOrderHistory.isEmpty()) {
                    JLabel noOrdersLabel = new JLabel("Δεν υπάρχουν παραγγελίες.");
                    noOrdersLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                    noOrdersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    historyPanel.add(noOrdersLabel);
                } else {

                    for (Order order : updatedOrderHistory) {
                        JPanel orderPanel = new JPanel();
                        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
                        orderPanel.setBorder(BorderFactory.createTitledBorder("Ημερομηνία: " + order.getOrderDate()));

                        for (Map.Entry<Product, Number> entry : order.getCart().getItems().entrySet()) {
                            Product product = entry.getKey();
                            Number quantity = entry.getValue();
                            JLabel productLabel = new JLabel("- " + product.getTitle() + " (Ποσότητα: " + quantity + ")");
                            productLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                            orderPanel.add(productLabel);
                        }

                        JLabel totalCostLabel = new JLabel("Συνολικό Κόστος: €" + String.format("%.2f", order.getTotalCost()));
                        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 14));
                        orderPanel.add(Box.createVerticalStrut(5));
                        orderPanel.add(totalCostLabel);

                        historyPanel.add(orderPanel);
                        historyPanel.add(Box.createVerticalStrut(10));
                    }
                }

                historyPanel.revalidate();
                historyPanel.repaint();
            }
        });

        return panel;
    }


    /**
     * Settαρει τα panels ανάλογα το ρόλο του χρήστη {@link UserRole}
     * @param mainPanel
     * @param frame
     * @param user
     */
    public void setPanelsBasedOnRole(JPanel mainPanel, JFrame frame, User user) {
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();

        mainPanel.removeAll();

        JPanel signInPanel = createSignInPanel(mainPanel, frame);
        JPanel registerPanel = createRegisterPanel(mainPanel);

        mainPanel.add(signInPanel, "Είσοδος");
        mainPanel.add(registerPanel, "Εγγραφή");

        if (user != null && user.getRole() == UserRole.ADMIN) {
            JPanel adminPanel = createAdminPanel(mainPanel, productDropdown);
            mainPanel.add(adminPanel, "Διαχειριστής");
            cardLayout.show(mainPanel, "Διαχειριστής");
        } else {
            JPanel userPanel = createUserPanel(mainPanel, productDropdown);
            mainPanel.add(userPanel, "Πελάτης");
            cardLayout.show(mainPanel, "Πελάτης");
        }

    }

}
