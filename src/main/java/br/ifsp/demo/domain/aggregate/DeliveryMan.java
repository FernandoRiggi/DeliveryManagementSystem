package br.ifsp.demo.domain.aggregate;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryMan {
    private UUID id;
    private String name;
    private int capacity;

    private DeliveryMan() {}

    public DeliveryMan(String name, int capacity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.capacity = capacity;
    }

    public static DeliveryMan reconstitute(UUID id, String name, int capacity) {
        DeliveryMan deliveryMan = new DeliveryMan();
        deliveryMan.id = id;
        deliveryMan.name = name;
        deliveryMan.capacity = capacity;
        return deliveryMan;
    }

    public void decreaseCapacity() {
        this.capacity--;
    }

    public void increaseCapacity() {
        this.capacity++;
    }
}
