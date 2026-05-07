package br.ifsp.demo.domain.valueObject;

import br.ifsp.demo.annotation.Structural;
import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.PriorityLevel;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static br.ifsp.demo.domain.valueObject.CustomerType.REGULAR;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LogisticScoreTest {


    @TDD
    @Test
    @DisplayName("[#30] Should create a NORMAL priority - Regular customer, 0 orders, 8km, 10Min")
    void shouldCreateNormalPriorityWhenRegularCustomer(){
        LogisticScore score = LogisticScore.calculate(REGULAR, 0,8.0, 10);

        assertThat(score.getPriorityLevel()).isEqualTo(PriorityLevel.NORMAL);
    }

    @TDD
    @Test
    @DisplayName("[#38] Should never be a negative value")
    void shouldNeverBeNegativeValue(){
        LogisticScore score = LogisticScore.calculate(REGULAR, 3,10.0, 5);

        assertThat(score.value()).isGreaterThanOrEqualTo(0);
    }

    @TDD
    @Test
    @DisplayName("[#32] Should create a URGENT priority - Premium customer, 0 orders, 10km, 15Min")
    void shouldCreateUrgentPriority(){
        LogisticScore score = LogisticScore.calculate(CustomerType.PREMIUM, 0,10.0, 15);

        assertThat(score.getPriorityLevel()).isEqualTo(PriorityLevel.URGENT);
    }

    @TDD
    @Test
    @DisplayName("[#31] Should create a Normal priority but with penalty - Regular customer, 2 orders, 10km, 10Min")
    void shouldCreateNormalPriorityButWithPenalty(){
        LogisticScore score = LogisticScore.calculate(CustomerType.REGULAR, 2,10.0, 10);

        assertThat(score.getPriorityLevel()).isEqualTo(PriorityLevel.NORMAL);
    }

    @TDD
    @Test
    @DisplayName("[#33] Should create a URGENT priority - Premium customer, 1 orders, 5km, 30Min")
    void shouldCreateUrgentForPremiumCustomer(){
        LogisticScore score = LogisticScore.calculate(CustomerType.PREMIUM, 1,5.0, 30);

        assertThat(score.getPriorityLevel()).isEqualTo(PriorityLevel.URGENT);
    }

    @TDD
    @Test
    @DisplayName("[#34] Should create a CRITICAL priority - Premium customer, 0 orders, 3km, 60Min")
    void shouldCreateCriticalWithNoPenalty(){
        LogisticScore score = LogisticScore.calculate(CustomerType.PREMIUM, 0,3.0, 60);

        assertThat(score.getPriorityLevel()).isEqualTo(PriorityLevel.CRITICAL);
    }

    @TDD
    @Test
    @DisplayName("[#35] Should create a CRITICAL priority - Premium customer, 4 orders, 3km, 80Min")
    void shouldCreateCriticalWithPenalty(){
        LogisticScore score = LogisticScore.calculate(CustomerType.PREMIUM, 4,3.0, 80);

        assertThat(score.getPriorityLevel()).isEqualTo(PriorityLevel.CRITICAL);
    }

    @TDD
    @Test
    @DisplayName("[#36] When waiting time increases, priority classification must update")
    void shouldUpdatePriorityWhenWaitingTimeIncreases() {
        LogisticScore before = LogisticScore.calculate(CustomerType.REGULAR, 0, 8.0, 10);
        LogisticScore after  = LogisticScore.calculate(CustomerType.REGULAR, 0, 8.0, 50);

        assertThat(before.getPriorityLevel()).isEqualTo(PriorityLevel.NORMAL);
        assertThat(after.getPriorityLevel()).isEqualTo(PriorityLevel.URGENT);
        assertThat(after.value()).isGreaterThan(before.value());
    }

    @TDD
    @Test
    @DisplayName("[#37] Higher score should have delivery priority over lower score")
    void shouldHaveHigherScoreHaveHigherPriority(){
        LogisticScore score1 = LogisticScore.calculate(CustomerType.REGULAR, 0, 8.0, 10);
        LogisticScore score2 = LogisticScore.calculate(CustomerType.PREMIUM, 0, 8.0, 10);

        assertThat(score2.isHigherThan(score1)).isTrue();
    }
}