package br.ifsp.demo.domain.valueObject;

import br.ifsp.demo.domain.aggregate.PriorityLevel;

public record LogisticScore(int value) {

    public LogisticScore {
        if(value < 0) value = 0;
    }

    public static LogisticScore calculate(CustomerType customerType, int activieOrders , double distance, int time) {
        int customerTypeValue = switch (customerType){
            case REGULAR -> 10;
            case PREMIUM -> 20;
            case BUSINESS -> 15;
        };

        int value = customerTypeValue + time - (activieOrders * (int) distance);
        return new LogisticScore(value);

    }

    public PriorityLevel GetPriorityLevel() {
        if(value >= 50) return PriorityLevel.CRITICAL;
        if(value >= 25) return PriorityLevel.URGENT;
        return PriorityLevel.NORMAL;
    }
}
