package controllers;

import models.Bill;
import models.Cashier;
import models.Item;
import models.Manager;
import models.Sector;
import models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginController {
    private final List<User> users = new ArrayList<>();
    private final List<Sector> sectors = new ArrayList<>();
    private final List<Bill> bills = new ArrayList<>(); // List to store bills

    public LoginController() {
        loadUsersFromFile("users.txt");
    }

    private void loadUsersFromFile(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] userData = line.split(",");
                if (userData.length >= 3) {
                    String username = userData[0].trim();
                    String password = userData[1].trim();
                    String role = userData[2].trim();

                    if (role.equalsIgnoreCase("Cashier")) {
                        Sector sector = getOrCreateSector(userData[3].trim());
                        Cashier cashier = new Cashier(username, password, sector);
                        sector.addCashier(cashier);
                        users.add(cashier);
                    } else if (role.equalsIgnoreCase("Manager")) {
                        List<Sector> managerSectors = new ArrayList<>();
                        for (String sectorName : userData[3].split(";")) {
                            Sector sector = getOrCreateSector(sectorName.trim());
                            if (!managerSectors.contains(sector)) {
                                managerSectors.add(sector);
                            }
                        }
                        Manager manager = new Manager(username, password, managerSectors);
                        users.add(manager);
                    } else if (role.equalsIgnoreCase("Administrator")) {
                        users.add(new User(username, password, "Administrator"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private Sector getOrCreateSector(String sectorName) {
        String trimmedSectorName = sectorName.trim(); // Trim whitespace but keep original case
        for (Sector sector : sectors) {
            if (sector.getName().equalsIgnoreCase(trimmedSectorName)) {
                return sector;
            }
        }
        // Create a new sector if not found
        Sector newSector = new Sector(trimmedSectorName); // Use the trimmed name directly
        sectors.add(newSector);
        return newSector;
    }

    public String login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user.getRole();
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    public List<Bill> getBills() {
        return bills;
    }
    
    public List<Item> getPurchasedItems() {
        List<Item> purchasedItems = new ArrayList<>();
        for (Bill bill : bills) {
            for (String purchasedItem : bill.getPurchasedItems()) {
                String[] parts = purchasedItem.split(" x| = \\$"); // Split on quantity and price
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    String quantity = parts[1].trim();
                    String price = parts[2].trim();
                    purchasedItems.add(new Item(name, price, quantity));
                }
            }
        }
        return purchasedItems;
    }


}