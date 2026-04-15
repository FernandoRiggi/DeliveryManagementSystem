package br.ifsp.demo.domain.valueObject;

import br.ifsp.demo.annotation.TDD;
import br.ifsp.demo.domain.aggregate.PriorityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static br.ifsp.demo.domain.valueObject.CustomerType.REGULAR;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LogisticScoreTest {


    @TDD
    @Test
    @DisplayName("[#30] Should create a NORMAL priority - Regular customer, 0 orders, 8km, 10Min")
    void ShouldCreateNormalPriorityWhenRegularCustomer(){
        LogisticScore score = LogisticScore.calculate(REGULAR, 0,8.0, 10);

        assertThat(score.GetPriorityLevel()).isEqualTo(PriorityLevel.NORMAL);
    }

}