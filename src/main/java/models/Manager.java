package models;

import java.util.List;

public class Manager extends User {
    private List<Sector> sectors;

    public Manager(String username, String password, List<Sector> sectors) {
        super(username, password, "Manager");
        this.sectors = sectors;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }
}