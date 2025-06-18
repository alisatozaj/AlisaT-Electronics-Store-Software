package views;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import controllers.ManagerController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Manager;
import models.Sector;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ManagerDashboard {
    private final Manager manager;
    private final ManagerController managerController;
    private Label notificationLabel;
    private BorderPane mainLayout;

    public ManagerDashboard(Manager manager) {
        this.manager = manager;
        this.managerController = new ManagerController(manager.getSectors());
    }

    public void show(Stage stage) {
        // Top Pane: Manager Information
        Label titleLabel = new Label("Manager Dashboard");
        titleLabel.setStyle("-fx-font-size: 35px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label managerNameLabel = new Label("Manager: " + manager.getUsername());
        managerNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label sectorsLabel = new Label("Sectors: " + 
            manager.getSectors().stream()
                .map(Sector::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("None")
        );

        HBox infoBox = new HBox(20, managerNameLabel, sectorsLabel);
        infoBox.setAlignment(Pos.CENTER);

        VBox topPane = new VBox(10, titleLabel, infoBox);
        topPane.setPadding(new Insets(10));
        topPane.setStyle("-fx-background-color: #1e90ff; -fx-text-fill: white;");
        topPane.setAlignment(Pos.CENTER);

        // Left Panel: Inventory Management Buttons
        Button viewInventoryButton = new Button("View Inventory");
        Button addNewItemButton = new Button("Add New Item");
        Button restockItemButton = new Button("Restock Item");
        Button monitorCashiersButton = new Button("Monitor Cashiers");
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");

        notificationLabel = new Label();
        notificationLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        VBox menuPanel = new VBox(10, viewInventoryButton, addNewItemButton, restockItemButton, monitorCashiersButton, logoutButton, notificationLabel);
        menuPanel.setPadding(new Insets(10));
        menuPanel.setStyle("-fx-background-color: #f0f0f0;");

        // Dynamic Content Area
        mainLayout = new BorderPane();
        TextArea dynamicContentArea = new TextArea();
        dynamicContentArea.setEditable(false);
        dynamicContentArea.setPromptText("Dynamic content will appear here.");
        dynamicContentArea.setStyle("-fx-font-size: 14px;");

        mainLayout.setTop(topPane);
        mainLayout.setLeft(menuPanel);
        mainLayout.setCenter(dynamicContentArea);

        Scene scene = new Scene(mainLayout, 800, 550);
        stage.setScene(scene);
        stage.setTitle("Manager Dashboard");
        stage.show();

        // Notify low stock on startup
        notifyLowStock();

        // Button Actions
        viewInventoryButton.setOnAction(e -> {
            StringBuilder inventoryDetails = new StringBuilder("Inventory:\n");

            try (Scanner scanner = new Scanner(new File("inventory.txt"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String itemName = parts[0].trim();
                        String price = parts[1].trim();
                        String quantity = parts[2].trim();
                        String sectorName = parts[3].trim();

                        boolean isManagedSector = manager.getSectors().stream()
                                .anyMatch(sector -> sector.getName().equalsIgnoreCase(sectorName));

                        if (isManagedSector) {
                            inventoryDetails.append("- ").append(itemName)
                                    .append(" (Price: $").append(price)
                                    .append(", Quantity: ").append(quantity)
                                    .append(", Sector: ").append(sectorName)
                                    .append(")\n");
                        }
                    }
                }
            } catch (Exception ex) {
                inventoryDetails.append("Error reading inventory: ").append(ex.getMessage());
            }

            TextArea inventoryArea = new TextArea(inventoryDetails.toString());
            inventoryArea.setEditable(false);
            inventoryArea.setStyle("-fx-font-size: 14px;");

            mainLayout.setCenter(inventoryArea);
        });

        addNewItemButton.setOnAction(e -> {
            VBox formContainer = new VBox(10);
            formContainer.setPadding(new Insets(10));

            TextField itemNameField = new TextField();
            itemNameField.setPromptText("Item Name");

            TextField itemPriceField = new TextField();
            itemPriceField.setPromptText("Price");

            TextField itemQuantityField = new TextField();
            itemQuantityField.setPromptText("Quantity");

            ComboBox<String> sectorComboBox = new ComboBox<>();
            manager.getSectors().forEach(sector -> sectorComboBox.getItems().add(sector.getName()));
            sectorComboBox.setPromptText("Select Sector");

            Button saveButton = new Button("Save");
            saveButton.setStyle("-fx-background-color: #1e90ff; -fx-text-fill: white;");

            formContainer.getChildren().addAll(
                new Label("Add New Item:"),
                new Label("Item Name:"), itemNameField,
                new Label("Price:"), itemPriceField,
                new Label("Quantity:"), itemQuantityField,
                new Label("Sector:"), sectorComboBox,
                saveButton
            );

            mainLayout.setCenter(formContainer);

            saveButton.setOnAction(saveEvent -> {
                String itemName = itemNameField.getText().trim();
                String itemPrice = itemPriceField.getText().trim();
                String itemQuantity = itemQuantityField.getText().trim();
                String sectorName = sectorComboBox.getValue();

                if (itemName.isEmpty() || itemPrice.isEmpty() || itemQuantity.isEmpty() || sectorName == null) {
                    formContainer.getChildren().add(new Label("Error: All fields are required!"));
                    return;
                }

                if (!itemPrice.matches("\\d+(\\.\\d{1,2})?") || !itemQuantity.matches("\\d+")) {
                    formContainer.getChildren().add(new Label("Error: Price must be a valid number and quantity must be an integer!"));
                    return;
                }

                try (PrintWriter writer = new PrintWriter(new FileOutputStream("inventory.txt", true))) {
                    writer.println(itemName + "," + itemPrice + "," + itemQuantity + "," + sectorName);
                    formContainer.getChildren().add(new Label("Success: Item added to inventory!"));
                    notifyLowStock();
                } catch (Exception ex) {
                    formContainer.getChildren().add(new Label("Error: Could not save item to inventory. " + ex.getMessage()));
                }
            });
        });

        restockItemButton.setOnAction(e -> {
            VBox formContainer = new VBox(10);
            formContainer.setPadding(new Insets(10));

            TextField itemNameField = new TextField();
            itemNameField.setPromptText("Item Name");

            TextField itemQuantityField = new TextField();
            itemQuantityField.setPromptText("Quantity");

            ComboBox<String> sectorComboBox = new ComboBox<>();
            manager.getSectors().forEach(sector -> sectorComboBox.getItems().add(sector.getName()));
            sectorComboBox.setPromptText("Select Sector");

            Button restockButton = new Button("Restock");
            restockButton.setStyle("-fx-background-color: #1e90ff; -fx-text-fill: white;");

            formContainer.getChildren().addAll(
                new Label("Restock Item:"),
                new Label("Item Name:"), itemNameField,
                new Label("Quantity:"), itemQuantityField,
                new Label("Sector:"), sectorComboBox,
                restockButton
            );

            mainLayout.setCenter(formContainer);

            restockButton.setOnAction(restockEvent -> {
                String itemName = itemNameField.getText().trim();
                String itemQuantity = itemQuantityField.getText().trim();
                String sectorName = sectorComboBox.getValue();

                if (itemName.isEmpty() || itemQuantity.isEmpty() || sectorName == null) {
                    formContainer.getChildren().add(new Label("Error: All fields are required!"));
                    return;
                }

                if (!itemQuantity.matches("\\d+")) {
                    formContainer.getChildren().add(new Label("Error: Quantity must be a positive integer!"));
                    return;
                }

                boolean itemFound = false;
                int newQuantity = Integer.parseInt(itemQuantity);

                try {
                    List<String> lines = new ArrayList<>();
                    File inventoryFile = new File("inventory.txt");
                    try (Scanner scanner = new Scanner(inventoryFile)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            String[] parts = line.split(",");
                            if (parts.length == 4) {
                                String existingItemName = parts[0].trim();
                                String price = parts[1].trim();
                                int quantity = Integer.parseInt(parts[2].trim());
                                String existingSectorName = parts[3].trim();

                                if (existingItemName.equalsIgnoreCase(itemName) && existingSectorName.equalsIgnoreCase(sectorName)) {
                                    quantity += newQuantity;
                                    itemFound = true;
                                }

                                lines.add(existingItemName + "," + price + "," + quantity + "," + existingSectorName);
                            }
                        }
                    }

                    if (itemFound) {
                        try (PrintWriter writer = new PrintWriter(inventoryFile)) {
                            for (String line : lines) {
                                writer.println(line);
                            }
                        }
                        formContainer.getChildren().add(new Label("Success: Item restocked successfully!"));
                        notifyLowStock();
                    } else {
                        formContainer.getChildren().add(new Label("Error: Item not found in the specified sector!"));
                    }

                } catch (Exception ex) {
                    formContainer.getChildren().add(new Label("Error: Could not update inventory. " + ex.getMessage()));
                }
            });
        });

        monitorCashiersButton.setOnAction(e -> { 
            StringBuilder cashierDetails = new StringBuilder("Cashier Performance:\n\n");

            // Retrieve only cashiers managing sectors assigned to this manager
            managerController.monitorCashiers().forEach(cashier -> {
                if (manager.getSectors().stream().anyMatch(sector -> sector.equals(cashier.getSector()))) {
                    // Get the current bill number
                    String currentBillNumber = cashier.getBills().isEmpty() 
                        ? "No bills yet" 
                        : cashier.getBills().get(cashier.getBills().size() - 1).getBillNumber();

                    cashierDetails.append("- Cashier: ").append(cashier.getUsername())
                            .append("\n  Sector: ").append(cashier.getSector().getName())
                            .append("\n  Total Bills Processed: ").append(cashier.getTotalBillsProcessed())
                            .append("\n  Total Revenue: $").append(String.format("%.2f", cashier.getTotalRevenue()))
                            .append("\n  Current Bill Number: ").append(currentBillNumber)
                            .append("\n\n");
                }
            });

            // If no cashiers are found
            if (cashierDetails.toString().equals("Cashier Performance:\n\n")) {
                cashierDetails.append("No cashiers found in managed sectors.");
            }

            // Display cashier details in a text area
            TextArea cashierArea = new TextArea(cashierDetails.toString());
            cashierArea.setEditable(false);
            cashierArea.setStyle("-fx-font-size: 14px;");
            cashierArea.setWrapText(true);

            mainLayout.setCenter(cashierArea); 
        });

        
      /*  monitorCashiersButton.setOnAction(e -> {
            StringBuilder cashierDetails = new StringBuilder("Cashier Performance:\n\n");

            // Retrieve only cashiers managing sectors assigned to this manager
            managerController.monitorCashiers().forEach(cashier -> {
                if (manager.getSectors().stream().anyMatch(sector -> sector.equals(cashier.getSector()))) {
                    cashierDetails.append("- Cashier: ").append(cashier.getUsername())
                            .append("\n  Sector: ").append(cashier.getSector().getName())
                            .append("\n  Total Bills Processed: ").append(cashier.getTotalBillsProcessed())
                            .append("\n  Total Revenue: $").append(String.format("%.2f", cashier.getTotalRevenue()))
                            .append("\n\n");
                }
            });

            // If no cashiers are found
            if (cashierDetails.toString().equals("Cashier Performance:\n\n")) {
                cashierDetails.append("No cashiers found in managed sectors.");
            }

            // Display in a TextArea in the center of the main layout
            TextArea cashierArea = new TextArea(cashierDetails.toString());
            cashierArea.setEditable(false);
            cashierArea.setStyle("-fx-font-size: 14px;");
            cashierArea.setWrapText(true);

            mainLayout.setCenter(cashierArea); // Replace center content with TextArea
        });*/

        logoutButton.setOnAction(e -> {
            // Reload the LoginView
            Stage primaryStage = (Stage) logoutButton.getScene().getWindow(); // Get the current stage
            LoginView loginView = new LoginView(); // Create a new instance of LoginView
            loginView.show(primaryStage); // Load the login page into the current stage
        });
    }

        private void notifyLowStock() {
            StringBuilder notifications = new StringBuilder();

            try (Scanner scanner = new Scanner(new File("inventory.txt"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String itemName = parts[0].trim();
                        int quantity = Integer.parseInt(parts[2].trim());
                        String sectorName = parts[3].trim();
                        int threshold = parts.length == 5 ? Integer.parseInt(parts[4].trim()) : 5; // Default threshold

                        // Check if the item belongs to a managed sector and is low in stock
                        if (manager.getSectors().stream().anyMatch(sector -> sector.getName().equalsIgnoreCase(sectorName))) {
                            if (quantity <= threshold) {
                                notifications.append("- ").append(itemName)
                                        .append(" (Quantity: ").append(quantity)
                                        .append(", Sector: ").append(sectorName)
                                        .append(")\n");
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                notifications.append("Error checking inventory: ").append(ex.getMessage());
            }

            // Display notification or clear the label if no low-stock items
            if (notifications.length() > 0) {
                notificationLabel.setText("Low stock for the following items:\n" + notifications);
            } else {
                notificationLabel.setText("");
            }
        }
    }
