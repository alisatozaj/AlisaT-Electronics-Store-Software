package models;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private static int billCounter = 1;
    private final String billNumber;
    private final List<String> purchasedItems = new ArrayList<>();
    private double totalAmount;

    public Bill() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = LocalDate.now().format(formatter);
        this.billNumber = "Bill" + datePart + "_" + billCounter;
        billCounter++;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<String> getPurchasedItems() {
        return purchasedItems;
    }

    public void addItem(String itemName, int quantity, double price) {
        purchasedItems.add(itemName + " x" + quantity + " = $" + String.format("%.2f", price * quantity));
        totalAmount += price * quantity;
    }

    public void saveToFile() {
        try {
            // Create a "bills" directory automatically if it doesn't exist
            File billsDir = new File("bills"); // This is relative to the project root
            if (!billsDir.exists()) {
                billsDir.mkdirs(); // Create the directory
            }

            // Save the bill as a text file inside the "bills" directory
            File billFile = new File(billsDir, billNumber + ".txt");
            try (PrintWriter writer = new PrintWriter(billFile)) {
                writer.println("Bill Number: " + billNumber);
                writer.println("Items Purchased:");
                for (String item : purchasedItems) {
                    writer.println("- " + item);
                }
                writer.println("Total Amount: $" + String.format("%.2f", totalAmount));
                writer.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        } catch (Exception e) {
            System.out.println("Error saving bill: " + e.getMessage());
        }
    }

 // Added method to clear the bill
    public void clearBill() {
        purchasedItems.clear();
        totalAmount = 0.0;
    }
}
