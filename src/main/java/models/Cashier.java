package models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cashier extends User {
    private Sector sector;
    private List<Bill> bills;
    private int totalBillsProcessed;
    private double totalRevenue;

    public Cashier(String username, String password, Sector sector) {
        super(username, password, "Cashier");
        this.sector = sector;
        this.bills = new ArrayList<>();
       // this.totalBillsProcessed = 0;
        this.totalRevenue = 0.0;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public int getTotalBillsProcessed() {
        File billsDir = new File("bills"); // The directory where bills are saved

        if (billsDir.exists() && billsDir.isDirectory()) {
            // Return the number of files in the directory
            return billsDir.listFiles((dir, name) -> name.endsWith(".txt")).length;
        } else {
            // If the directory doesn't exist or isn't a directory
            return 0;
        }
    }

  /*  public int getTotalBillsProcessed() {
        //return totalBillsProcessed;
    	return this.bills.size();
    }
*/
   
    public double getTotalRevenue() {
        File billsDir = new File("bills"); // The directory where bills are saved
        double totalRevenue = 0.0;

        if (billsDir.exists() && billsDir.isDirectory()) {
            File[] billFiles = billsDir.listFiles((dir, name) -> name.endsWith(".txt"));

            if (billFiles != null) {
                for (File billFile : billFiles) {
                    try (Scanner scanner = new Scanner(billFile)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.startsWith("Total Amount: $")) {
                                // Extract the revenue amount
                                String amountStr = line.replace("Total Amount: $", "").trim();
                                totalRevenue += Double.parseDouble(amountStr);
                                break; // Move to the next file after finding the total
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error reading bill file: " + billFile.getName() + " - " + e.getMessage());
                    }
                }
            }
        }

        return totalRevenue;
    }

    
    /* public double getTotalRevenue() {
        return totalRevenue;
    }*/

    public void addBill(Bill bill) {
        this.bills.add(bill);
      //  ++this.totalBillsProcessed;
        this.totalRevenue += bill.getTotalAmount();
    }
    
    
    /*public void addBill(Bill bill) {
        bills.add(bill);
        totalBillsProcessed++;
        totalRevenue += bill.getTotalAmount();
    }*/

    public List<Bill> getBills() {
        return bills;
    }
}
