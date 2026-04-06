package br.ifsp.demo.domain.aggregate;

public class Deliveryman {
    private String name;
    private int capacity;

    public Deliveryman(String name, int capacity) {
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
