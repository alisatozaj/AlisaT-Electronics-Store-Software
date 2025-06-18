package controllers;

import models.Bill;
import models.Cashier;
import models.Item;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CashierController {
    private final Cashier cashier;
    private final List<Item> inventory;
    private final List<Bill> dailyBills;

    public CashierController(Cashier cashier) {
        this.cashier = cashier;
        this.inventory = new ArrayList<>();
        this.dailyBills = new ArrayList<>();
        loadInventoryFromFile();
    }

    private void loadInventoryFromFile() {
        try (Scanner scanner = new Scanner(new File("inventory.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String itemName = parts[0].trim();
                    String price = parts[1].trim();
                    String quantity = parts[2].trim();
                    String sectorName = parts[3].trim();

                    if (sectorName.equalsIgnoreCase(cashier.getSector().getName())) {
                        inventory.add(new Item(itemName, price, quantity));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading inventory: " + e.getMessage());
        }
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean processPurchase(String itemName, int quantity, Bill bill) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                int currentStock = Integer.parseInt(item.getQuantity());
                if (currentStock >= quantity) {
                    item.setQuantity(String.valueOf(currentStock - quantity));
                    bill.addItem(itemName, quantity, Double.parseDouble(item.getPrice()));
                    return true;
                } else {
                     return false;
                }
            }
        }
        return false;
    }

    public void finalizeBill(Bill bill) {
        dailyBills.add(bill);
        cashier.addBill(bill); // Update the cashier's performance metrics
        bill.saveToFile(); // Ensure bills are saved
        saveInventoryToFile(); // Save updated inventory to the file
    }

    private void saveInventoryToFile() {
        try {
            // Read all lines from the file into a List
            List<String> lines = new ArrayList<>();
            try (Scanner scanner = new Scanner(new File("inventory.txt"))) {
                while (scanner.hasNextLine()) {
                    lines.add(scanner.nextLine());
                }
            }

            // Update the lines for items in the cashier's sector
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String itemName = parts[0].trim();
                    String sectorName = parts[3].trim();

                    // Check if the line matches an item in the cashier's sector
                    if (sectorName.equalsIgnoreCase(cashier.getSector().getName())) {
                        for (Item item : inventory) {
                            if (item.getName().equalsIgnoreCase(itemName)) {
                                // Update the line with the new quantity
                                lines.set(i, item.getName() + "," + item.getPrice() + "," + item.getQuantity() + "," + sectorName);
                                break;
                            }
                        }
                    }
                }
            }

            // Write the updated lines back to the file
            try (PrintWriter writer = new PrintWriter(new File("inventory.txt"))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Error saving inventory: " + e.getMessage());
        }
    }


    public List<Bill> getDailyBills() {
        return dailyBills;
    }

    public double getTotalDailySales() {
        double totalSales = 0.0;
        for (Bill bill : dailyBills) {
            totalSales += bill.getTotalAmount(); // Sum up the total amount of each bill
        }
        return totalSales;
    }

}
