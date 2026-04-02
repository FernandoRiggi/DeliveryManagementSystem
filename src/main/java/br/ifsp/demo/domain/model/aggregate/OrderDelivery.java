package br.ifsp.demo.domain.model.aggregate;

public class OrderDelivery {
    private StatusOrder statusOrder;

    public OrderDelivery(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
    }


    public void cancel() {
        if(this.statusOrder == StatusOrder.CONCLUDED) throw new IllegalStateException("[Order already concluded]");
        this.statusOrder = StatusOrder.CANCELED;
    }

    public StatusOrder getStatus() {
        return statusOrder;
    }

    public void setStatusOrder(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
    }
}
