package br.ifsp.demo.domain.valueObject;

import br.ifsp.demo.domain.aggregate.PriorityLevel;

public record LogisticScore(int score) {

    public static LogisticScore calculate(CustomerType customerType,int activieOrders ,double distance, int time) {
        int customerTypeValue = switch (customerType){
            case REGULAR -> 0;
            case PREMIUM -> 10;
            case BUSINESS -> 15;
        };

        int value = customerTypeValue + time - (activieOrders * (int) distance);
        return new LogisticScore(value);

    }

    public PriorityLevel GetPriorityLevel() {
        if(score >= 50) return PriorityLevel.CRITICAL;
        if(score >= 25) return PriorityLevel.URGENT;
        return PriorityLevel.NORMAL;
    }
}
