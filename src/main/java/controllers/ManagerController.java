package controllers;

import models.Cashier;
import models.Item;
import models.Sector;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ManagerController {
    private final List<Sector> managedSectors;

    public ManagerController(List<Sector> managedSectors) {
        this.managedSectors = managedSectors;
    }

    public List<Item> getInventoryForSectors() {
        List<Item> inventory = new ArrayList<>();
        for (Sector sector : managedSectors) {
            inventory.addAll(sector.getItems());
        }
        return inventory;
    }

    public void addNewItem(String itemName, double price, int quantity, String sectorName) {
        for (Sector sector : managedSectors) {
            if (sector.getName().equalsIgnoreCase(sectorName)) {
                sector.getItems().add(new Item(itemName, String.valueOf(price), String.valueOf(quantity)));
                saveInventoryToFile();
                return;
            }
        }
    }

    public void restockItem(String itemName, int quantity, String sectorName) {
        for (Sector sector : managedSectors) {
            if (sector.getName().equalsIgnoreCase(sectorName)) {
                for (Item item : sector.getItems()) {
                    if (item.getName().equalsIgnoreCase(itemName)) {
                        int currentQuantity = Integer.parseInt(item.getQuantity());
                        item.setQuantity(String.valueOf(currentQuantity + quantity));
                        saveInventoryToFile();
                        return;
                    }
                }
            }
        }
    }

    public void saveInventoryToFile() {
        try (PrintWriter writer = new PrintWriter(new File("inventory.txt"))) {
            for (Sector sector : managedSectors) {
                for (Item item : sector.getItems()) {
                    writer.println(item.getName() + "," + item.getPrice() + "," + item.getQuantity() + "," + sector.getName());
                }
            }
        } catch (Exception e) {
            System.out.println("Error saving inventory: " + e.getMessage());
        }
    }

    public List<Cashier> monitorCashiers() {
        List<Cashier> cashiers = new ArrayList<>();
        for (Sector sector : managedSectors) {
            for (Cashier cashier : sector.getCashiers()) {
                if (!cashiers.contains(cashier)) {
                    cashiers.add(cashier);
                }
            }
        }
        return cashiers;
    }

    public List<String> getCashierPerformance() {
        List<String> performanceList = new ArrayList<>();
        for (Sector sector : managedSectors) {
            for (Cashier cashier : sector.getCashiers()) {
                performanceList.add(
                    "Cashier: " + cashier.getUsername() +
                    "\n  Sector: " + sector.getName() +
                    "\n  Total Bills Processed: " + cashier.getTotalBillsProcessed() +
                    "\n  Total Revenue: $" + String.format("%.2f", cashier.getTotalRevenue())
                );
            }
        }
        return performanceList;
    }


    public void generateReport(String period) {
        // Report generation logic (daily, monthly, etc.)
        // Placeholder for now
        System.out.println("Report generated for period: " + period);
    }
}