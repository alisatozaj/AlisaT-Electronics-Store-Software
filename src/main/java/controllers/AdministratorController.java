// AdministratorController.java
package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Employee;
import models.Bill;
import models.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class AdministratorController {
    private final ObservableList<Employee> employeeList;
    private final ObservableList<Bill> billList;
    private final ObservableList<Item> purchasedItems;

    public AdministratorController(List<Bill> bills, List<Item> purchasedItems) {
        this.employeeList = FXCollections.observableArrayList();
        this.billList = FXCollections.observableArrayList(bills);
        this.purchasedItems = FXCollections.observableArrayList(purchasedItems);
        loadEmployeesFromFile("users.txt");
    }

    private void loadEmployeesFromFile(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String username = parts[0].trim();
                    String password = parts[1].trim(); // Not used in Employee
                    String role = parts[2].trim();
                    String sectors = parts[3].trim();

                    employeeList.add(new Employee(username, role, username + "@example.com", "000-000-0000", "0"));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Could not load employees from file: " + e.getMessage());
        }
    }

    public ObservableList<Employee> getEmployeeList() {
        return employeeList;
    }

    public String getEmployeeListDetails() {
        StringBuilder details = new StringBuilder();
        for (Employee employee : employeeList) {
            details.append("Name: ").append(employee.getName()).append("\n")
                   .append("Role: ").append(employee.getRole()).append("\n")
                   .append("Email: ").append(employee.getEmail()).append("\n")
                   .append("Phone: ").append(employee.getPhoneNumber()).append("\n")
                   .append("Salary: $").append(employee.getSalary()).append("\n\n");
        }
        return details.toString();
    }

    public void addEmployee(Employee newEmployee) {
        employeeList.add(newEmployee);
        saveEmployeeToFile(newEmployee, "users.txt");
    }

    
    private void saveEmployeeToFile(Employee employee, String fileName) {
        try (FileWriter fw = new FileWriter(fileName, true); PrintWriter writer = new PrintWriter(fw)) {
            String role = employee.getRole().equalsIgnoreCase("Manager") ? "Manager" : "Cashier";
            writer.println(employee.getName() + "," +
                    role + "," +
                    (role.equals("Manager") ? "Mobile Devices;Audio & Video" : "Computers") + "," + 
                    employee.getEmail() + "," +
                    employee.getPhoneNumber() + "," +
                    employee.getSalary());
        } catch (Exception e) {
            System.out.println("Error saving employee: " + e.getMessage());
        }
    }
    
    public void modifyEmployee(Employee oldEmployee, Employee modifiedEmployee) {
        if (employeeList.contains(oldEmployee)) {
            oldEmployee.setName(modifiedEmployee.getName());
            oldEmployee.setRole(modifiedEmployee.getRole());
            oldEmployee.setEmail(modifiedEmployee.getEmail());
            oldEmployee.setPhoneNumber(modifiedEmployee.getPhoneNumber());
            oldEmployee.setSalary(modifiedEmployee.getSalary());
        }
    }

    public boolean deleteEmployeeByName(String name) {
        Employee employeeToRemove = findEmployeeByName(name);
        if (employeeToRemove != null) {
            employeeList.remove(employeeToRemove);
            return true;
        }
        return false;
    }

    public Employee findEmployeeByName(String name) {
        for (Employee employee : employeeList) {
            if (employee.getName().equalsIgnoreCase(name)) {
                return employee;
            }
        }
        return null;
    }

    public String generateSalesReport() {
        double totalIncome = 0.0; // Total value of items sold
        double totalCost = calculateTotalCosts();

        // Calculate total income from bills
        for (Bill bill : billList) {
            totalIncome += bill.getTotalAmount();
        }

        return "Total Income: $" + String.format("%.2f", totalIncome) + "\n" +
               "Total Costs: $" + String.format("%.2f", totalCost) + "\n" +
               "Net Profit: $" + String.format("%.2f", (totalIncome - totalCost));
    }

    public String generateExpensesReport() {
        double totalSalaries = 0.0;
        double totalPurchases = 0.0;

        // Calculate total salaries
        for (Employee employee : employeeList) {
            totalSalaries += Double.parseDouble(employee.getSalary());
        }

        // Calculate total purchase costs
        for (Item item : purchasedItems) {
            totalPurchases += Double.parseDouble(item.getPrice()) * Double.parseDouble(item.getQuantity());
        }

        return "Total Salaries: $" + String.format("%.2f", totalSalaries) + "\n" +
               "Total Purchases: $" + String.format("%.2f", totalPurchases) + "\n" +
               "Total Costs: $" + String.format("%.2f", (totalSalaries + totalPurchases));
    }

    private double calculateTotalCosts() {
        double totalSalaries = 0.0;
        double totalPurchases = 0.0;

        for (Employee employee : employeeList) {
            totalSalaries += Double.parseDouble(employee.getSalary());
        }

        for (Item item : purchasedItems) {
            totalPurchases += Double.parseDouble(item.getPrice()) * Double.parseDouble(item.getQuantity());
        }

        return totalSalaries + totalPurchases;
    }

    public ObservableList<Bill> getBills() {
        return billList;
    }
}
