// AdministratorDashboard.java
package views;

import controllers.AdministratorController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Employee;

import java.util.Optional;

public class AdministratorDashboard {
    private final AdministratorController adminController;
    private final String adminUsername;

    public AdministratorDashboard(AdministratorController adminController, String adminUsername) {
        this.adminController = adminController;
        this.adminUsername = adminUsername;
    }


    public void show(Stage stage) {
        // Top Pane: Administrator Information
        Label titleLabel = new Label("Administrator Dashboard");
        titleLabel.setStyle("-fx-font-size: 35px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label adminNameLabel = new Label("Administrator: " + adminUsername);
        adminNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        VBox topPane = new VBox(10, titleLabel, adminNameLabel);
        topPane.setPadding(new Insets(10));
        topPane.setStyle("-fx-background-color: #1e90ff; -fx-text-fill: white;");
        topPane.setAlignment(Pos.CENTER);

        // Left Panel: Main Menu Buttons
        Button manageEmployeesButton = new Button("Manage Employees");
        Button viewSalesReportButton = new Button("View Sales Report");
        Button viewExpensesButton = new Button("View Expenses");
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;");
        
        VBox menuPanel = new VBox(10, manageEmployeesButton, viewSalesReportButton, viewExpensesButton);
        menuPanel.setPadding(new Insets(10));
        menuPanel.setStyle("-fx-background-color: #f0f0f0;");
		menuPanel.getChildren().add(logoutButton);
        // Center Pane: Dynamic Content Area
        BorderPane dynamicContentArea = new BorderPane();

        // Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topPane);
        mainLayout.setLeft(menuPanel);
        mainLayout.setCenter(dynamicContentArea);

        Scene scene = new Scene(mainLayout, 800, 550);
        stage.setScene(scene);
        stage.setTitle("Administrator Dashboard");
        stage.show();

        // Manage Employees Action
        manageEmployeesButton.setOnAction(e -> {
            VBox employeeManagementPane = new VBox(10);
            employeeManagementPane.setPadding(new Insets(10));

            Label header = new Label("Manage Employees");
            header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            TextArea employeeListArea = new TextArea();
            employeeListArea.setEditable(false);
            employeeListArea.setText(adminController.getEmployeeListDetails());

            Button addButton = new Button("Add Employee");
            Button modifyButton = new Button("Modify Employee");
            Button deleteButton = new Button("Delete Employee");

            HBox actionButtons = new HBox(10, addButton, modifyButton, deleteButton);
            actionButtons.setAlignment(Pos.CENTER);

            employeeManagementPane.getChildren().addAll(header, employeeListArea, actionButtons);

            dynamicContentArea.setCenter(employeeManagementPane);

            // Add Employee Action
            addButton.setOnAction(addEvent -> {
                Employee newEmployee = createEmployeeDialog("Add Employee", null);
                if (newEmployee != null) {
                    adminController.addEmployee(newEmployee);
                    employeeListArea.setText(adminController.getEmployeeListDetails());
                }
            });

            // Modify Employee Action
            modifyButton.setOnAction(modifyEvent -> {
                String selectedEmployeeName = showTextInputDialog("Modify Employee", "Enter the name of the employee to modify:");
                if (selectedEmployeeName != null) {
                    Employee employee = adminController.findEmployeeByName(selectedEmployeeName);
                    if (employee != null) {
                        Employee modifiedEmployee = createEmployeeDialog("Modify Employee", employee);
                        if (modifiedEmployee != null) {
                            adminController.modifyEmployee(employee, modifiedEmployee);
                            employeeListArea.setText(adminController.getEmployeeListDetails());
                        }
                    } else {
                        showAlert("Error", "Employee not found.");
                    }
                }
            });

            // Delete Employee Action
            deleteButton.setOnAction(deleteEvent -> {
                String selectedEmployeeName = showTextInputDialog("Delete Employee", "Enter the name of the employee to delete:");
                if (selectedEmployeeName != null) {
                    boolean success = adminController.deleteEmployeeByName(selectedEmployeeName);
                    if (success) {
                        employeeListArea.setText(adminController.getEmployeeListDetails());
                    } else {
                        showAlert("Error", "Employee not found.");
                    }
                }
            });
        });

        // View Sales Report Action
        viewSalesReportButton.setOnAction(e -> {
            VBox salesReportPane = new VBox(10);
            salesReportPane.setPadding(new Insets(10));

            Label header = new Label("Sales Report");
            header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            TextArea reportArea = new TextArea();
            reportArea.setEditable(false);
            reportArea.setText(adminController.generateSalesReport());

            salesReportPane.getChildren().addAll(header, reportArea);
            dynamicContentArea.setCenter(salesReportPane);
        });

        // View Expenses Action
        viewExpensesButton.setOnAction(e -> {
            VBox expensesReportPane = new VBox(10);
            expensesReportPane.setPadding(new Insets(10));

            Label header = new Label("Expenses Report");
            header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            TextArea expensesArea = new TextArea();
            expensesArea.setEditable(false);
            expensesArea.setText(adminController.generateExpensesReport());

            expensesReportPane.getChildren().addAll(header, expensesArea);
            dynamicContentArea.setCenter(expensesReportPane);
        });
        
        logoutButton.setOnAction(e -> {
            Stage primaryStage = (Stage) logoutButton.getScene().getWindow(); // Get the current stage
            LoginView loginView = new LoginView(); // Create a new instance of LoginView
            loginView.show(primaryStage); // Load the login page into the current stage
        });

    }

    private Employee createEmployeeDialog(String title, Employee existingEmployee) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Dialog Content
        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(10));

        TextField nameField = new TextField(existingEmployee != null ? existingEmployee.getName() : "");
        nameField.setPromptText("Name");

        TextField roleField = new TextField(existingEmployee != null ? existingEmployee.getRole() : "");
        roleField.setPromptText("Role");

        TextField emailField = new TextField(existingEmployee != null ? existingEmployee.getEmail() : "");
        emailField.setPromptText("Email");

        TextField phoneField = new TextField(existingEmployee != null ? existingEmployee.getPhoneNumber() : "");
        phoneField.setPromptText("Phone Number");

        TextField salaryField = new TextField(existingEmployee != null ? existingEmployee.getSalary() : "");
        salaryField.setPromptText("Salary");

        dialogContent.getChildren().addAll(
            new Label("Name:"), nameField,
            new Label("Role:"), roleField,
            new Label("Email:"), emailField,
            new Label("Phone Number:"), phoneField,
            new Label("Salary:"), salaryField
        );

        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Employee(
                    nameField.getText(),
                    roleField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    salaryField.getText()
                );
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private String showTextInputDialog(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setContentText(content);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
