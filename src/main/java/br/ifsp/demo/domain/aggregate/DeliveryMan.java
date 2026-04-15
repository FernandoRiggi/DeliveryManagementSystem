package br.ifsp.demo.domain.aggregate;

import java.util.UUID;

public class DeliveryMan {
    private UUID id;
    private String name;
    private int capacity;

    public DeliveryMan(String name, int capacity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.capacity = capacity;
    }

    public DeliveryMan(UUID id, String name, int capacity) {
        this.id = id;
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
