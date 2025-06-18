package models;

import java.util.ArrayList;
import java.util.List;

public class Sector {
    private String name;
    private List<Item> items;
    private List<Cashier> cashiers;

    public Sector(String name) {
        this.name = name;
        this.items = new ArrayList<>();
        this.cashiers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Cashier> getCashiers() {
        return cashiers;
    }

    public void addCashier(Cashier cashier) {
        cashiers.add(cashier);
    }

    @Override
    public String toString() {
        return "Sector: " + name + ", Items: " + items.size() + ", Cashiers: " + cashiers.size();
    }
}
