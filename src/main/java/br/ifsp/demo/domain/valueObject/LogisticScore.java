package br.ifsp.demo.domain.valueObject;

import br.ifsp.demo.domain.aggregate.PriorityLevel;

public record LogisticScore(int value) {

    public LogisticScore {
        if(value < 0) value = 0;
    }

    public static LogisticScore calculate(CustomerType customerType, int activeOrders , double distance, int time) {
        int score = switch (customerType){
            case REGULAR -> 10;
            case BUSINESS -> 20;
            case PREMIUM -> 30;
        };

        if(distance <= 3) {
            score += 20;
        } else if(distance <= 8) {
            score += 10;
        } else score += 0;

        if (time <= 15) {
            score += 0;
        } else if (time <= 30) {
            score += 10;
        } else if (time <= 60) {
            score += 20;
        } else {
            score += 30;
        }

        if (activeOrders <= 1) {
            score += 0;
        } else if (activeOrders == 2) {
            score -= 10;
        } else {
            score -= 20;
        }


        return new LogisticScore(score);

    }

    public PriorityLevel getPriorityLevel() {
        if(value >= 60) return PriorityLevel.CRITICAL;
        if(value >= 30) return PriorityLevel.URGENT;
        return PriorityLevel.NORMAL;
    }

    public boolean isHigherThan(LogisticScore other) {
        return this.value > other.value;
    }

}
