package br.ifsp.demo.exception;

public class DeliveryManNotFoundException extends RuntimeException {
    public DeliveryManNotFoundException(String message) {
        super(message);
    }
}
