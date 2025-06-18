package views;

import java.util.List;

import controllers.AdministratorController;
import controllers.LoginController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.Bill;
import models.Cashier;
import models.Item;
import models.Manager;
import models.User;
import javafx.geometry.Insets;

public class LoginView {
    private final LoginController loginController;

    public LoginView() {
        this.loginController = new LoginController();
    }

    public void show(Stage stage) {
        Label titleLabel = new Label("Electronics Store\nSoftware");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 35));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        // Username
        VBox usernameBox = new VBox();
        usernameBox.setSpacing(10);
        usernameBox.setAlignment(Pos.CENTER_LEFT);
        usernameBox.setStyle("-fx-background-color: transparent;");

        Label usernameLabel = new Label("Username: ");
        usernameLabel.setAlignment(Pos.TOP_LEFT);
        usernameLabel.setTextAlignment(TextAlignment.LEFT);
        usernameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        usernameLabel.setTextFill(Color.WHITE);

        TextField usernameField = new TextField();
        usernameField.setPrefWidth(250); 
        usernameField.setPrefHeight(40);
        usernameField.setPromptText("Enter your username");
        usernameField.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        usernameField.setStyle("-fx-border-radius: 15px; -fx-background-radius: 10px;");

        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password
        VBox passwordBox = new VBox();
        passwordBox.setSpacing(10);
        passwordBox.setAlignment(Pos.CENTER_LEFT); 
        passwordBox.setStyle("-fx-background-color: transparent;");

        Label passwordLabel = new Label("Password: ");
        passwordLabel.setAlignment(Pos.TOP_LEFT);
        passwordLabel.setTextAlignment(TextAlignment.LEFT);
        passwordLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        passwordLabel.setTextFill(Color.WHITE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(250); 
        passwordField.setPrefHeight(40);
        passwordField.setPromptText("Enter your password");
        passwordField.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        passwordField.setStyle("-fx-border-radius: 15px; -fx-background-radius: 10px;");

        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(70);
        loginButton.setStyle("-fx-background-color: #ffffff;");
        loginButton.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));


        // error message label (Initially hidden)
        Label errorLabel = new Label("Invalid login credentials!");
        errorLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        errorLabel.setTextFill(Color.WHITE);
        errorLabel.setStyle("-fx-background-color: #ff4d4d; -fx-padding: 5;");
        errorLabel.setVisible(false);  // hidden

        // a container VBox to hold the form with a fixed width
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: transparent;");
        container.setMaxWidth(400);
        container.setPadding(new Insets(20));

        // Add inside the container title label, username, password boxes, login button and error label
        container.getChildren().addAll(titleLabel, usernameBox, passwordBox, loginButton, errorLabel);

        // Main layout
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);  // centers everything
        layout.setPadding(new Insets(50));
        layout.getChildren().add(container);

        // bg color
        layout.setStyle("-fx-background-color: #1e90ff;");

        // scene with the layout
        Scene scene = new Scene(layout, 800, 550);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();

        //handle login button click
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Call LoginController for validation
            String userRole = loginController.login(username, password); // Get user role

            if (userRole != null) {
                // Retrieve user object based on role and username
                User loggedInUser = loginController.getUserByUsername(username); // Implement thisnController

                // Role-based redirection to appropriate dashboard
                switch (userRole) {
                    case "Cashier":
                        if (loggedInUser instanceof Cashier) {
                            Cashier cashier = (Cashier) loggedInUser;
                            CashierDashboard cashierDashboard = new CashierDashboard(cashier);
                            cashierDashboard.show(stage);
                        }
                        break;
                    case "Manager":
                        if (loggedInUser instanceof Manager) {
                            Manager manager = (Manager) loggedInUser;
                            ManagerDashboard managerDashboard = new ManagerDashboard(manager);
                            managerDashboard.show(stage);
                        }
                        break;
                    case "Administrator":
                    	List<Bill> bills = loginController.getBills(); // Ensure this method exists
                        List<Item> purchasedItems = loginController.getPurchasedItems(); // Ensure this method exists
                        AdministratorController adminController = new AdministratorController(bills,purchasedItems);
                        AdministratorDashboard adminDashboard = new AdministratorDashboard(adminController,loggedInUser.getUsername());
                        adminDashboard.show(stage);
                        break;
                    default:
                        System.out.println("Invalid role");
                }
            } else {
                // Show error message if login fails
                errorLabel.setVisible(true);  // Show the error label
                System.out.println("Invalid login credentials");
            }
        });
    }
}
