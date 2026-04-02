package br.ifsp.demo.domain.model.aggregate;

public class OrderDelivery {
    private StatusOrder statusOrder;

    public OrderDelivery(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
    }


    public void cancel() {
        this.statusOrder = StatusOrder.CANCELED;
    }

    public StatusOrder getStatus() {
        return statusOrder;
    }
}
