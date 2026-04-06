package br.ifsp.demo.domain.aggregate;

public class DeliveryMan {
    private String name;
    private int capacity;

    public DeliveryMan(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }
}
