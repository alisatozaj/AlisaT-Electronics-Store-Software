// Employee.java
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Employee {
    private final StringProperty name;
    private final StringProperty role;
    private final StringProperty email;
    private final StringProperty phoneNumber;
    private final StringProperty salary;

    public Employee(String name, String role, String email) {
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.email = new SimpleStringProperty(email);
        this.phoneNumber = new SimpleStringProperty("");
        this.salary = new SimpleStringProperty("");
    }

    public Employee(String name, String role, String email, String phoneNumber, String salary) {
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.email = new SimpleStringProperty(email);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.salary = new SimpleStringProperty(salary);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public StringProperty roleProperty() {
        return role;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public String getSalary() {
        return salary.get();
    }

    public void setSalary(String salary) {
        this.salary.set(salary);
    }

    public StringProperty salaryProperty() {
        return salary;
    }
}
