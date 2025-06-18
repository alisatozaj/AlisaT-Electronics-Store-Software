package views;

import controllers.CashierController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.Bill;
import models.Cashier;

public class CashierDashboard {
    private final CashierController cashierController;
    private final Cashier cashier;
    private Bill currentBill; // Declare currentBill as a class-level field

    // Right Panel UI Components
    private ListView<String> billsList;
    private Label totalSalesLabel;

    public CashierDashboard(Cashier cashier) {
        this.cashier = cashier;
        this.cashierController = new CashierController(cashier);
        this.currentBill = null; // Initialize as null
    }

    public void show(Stage stage) {
        // Top Pane
        StackPane topPane = new StackPane();
        topPane.setStyle("-fx-background-color: #1e90ff;");
        topPane.setPrefHeight(80);

        Label titleLabel = new Label("Cashier Dashboard");
        titleLabel.setStyle("-fx-font-size: 35px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label sectorLabel = new Label("Sector: " + cashier.getSector().getName());
        sectorLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        VBox topContent = new VBox(titleLabel, sectorLabel);
        topContent.setSpacing(10);
        topPane.getChildren().add(topContent);

        // Left Panel
        Label itemNameLabel = new Label("Item:");
        TextField itemNameField = new TextField();
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();

        Button addButton = new Button("Add Item");
        Button clearButton = new Button("Clear Bill");

        TextArea billDetailsArea = new TextArea();
        billDetailsArea.setEditable(false);

        Label totalLabel = new Label("Total: $0.00");
        Button finalizeButton = new Button("Generate Bill");
        Button logoutButton = new Button("Logout");

        addButton.setOnAction(e -> {
            if (currentBill == null) {
                currentBill = new Bill(); // Create a new Bill when adding the first item
            }

            String itemName = itemNameField.getText().trim();
            String quantityStr = quantityField.getText().trim();

            if (!itemName.isEmpty() && !quantityStr.isEmpty() && quantityStr.matches("\\d+")) {
                int quantity = Integer.parseInt(quantityStr);
                boolean itemExists = cashierController.getInventory().stream()
                    .anyMatch(item -> item.getName().equalsIgnoreCase(itemName));

                if (!itemExists) {
                    showAlert("Item not found in your sector!");
                    return;
                }

                boolean success = cashierController.processPurchase(itemName, quantity, currentBill);

                if (success) {
                    billDetailsArea.setText(String.join("\n", currentBill.getPurchasedItems()));
                    totalLabel.setText("Total: $" + String.format("%.2f", currentBill.getTotalAmount()));
                } else {
                    showAlert("Insufficient stock for the selected item.");
                }
            } else {
                showAlert("Invalid input. Please enter a valid item name and quantity.");
            }
        });

        clearButton.setOnAction(e -> {
            if (currentBill != null) {
                currentBill.clearBill();
            }
            billDetailsArea.clear();
            totalLabel.setText("Total: $0.00");
        });

        finalizeButton.setOnAction(e -> {
            if (currentBill.getPurchasedItems().isEmpty()) {
                showAlert("No items added to the bill!");
                return;
            }

            cashierController.finalizeBill(currentBill);

            // Save the finalized bill to a file
            currentBill.saveToFile();

            billsList.getItems().add(currentBill.getBillNumber() + " - $" + String.format("%.2f", currentBill.getTotalAmount()));
            totalSalesLabel.setText("Total Sales: $" + String.format("%.2f", cashierController.getTotalDailySales()));

            showAlert("Bill " + currentBill.getBillNumber() + " finalized and saved.");
            currentBill = new Bill(); // Create a new Bill for the next transaction
            billDetailsArea.clear();
            totalLabel.setText("Total: $0.00");
        });

        logoutButton.setOnAction(e -> {
            Stage primaryStage = (Stage) logoutButton.getScene().getWindow();
            new LoginView().show(primaryStage);
        });

        VBox leftPanel = new VBox(10, itemNameLabel, itemNameField, quantityLabel, quantityField, addButton, clearButton, billDetailsArea, totalLabel, finalizeButton, logoutButton);
        leftPanel.setPadding(new Insets(10));

        Label todaySalesLabel = new Label("Today's Sales");
        billsList = new ListView<>();
        totalSalesLabel = new Label("Total Sales: $0.00");

        VBox rightPanel = new VBox(10, todaySalesLabel, billsList, totalSalesLabel);
        rightPanel.setPadding(new Insets(10));

        SplitPane splitPane = new SplitPane(leftPanel, rightPanel);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topPane);
        mainLayout.setCenter(splitPane);

        Scene scene = new Scene(mainLayout, 800, 550);
        stage.setScene(scene);
        stage.setTitle("Cashier Dashboard");
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
    }
}

