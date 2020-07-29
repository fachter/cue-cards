package com.project.cuecards.useCases;

import com.project.cuecards.exceptions.DivideByZeroException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProgTestTest {

    private ProgTest progTest;

    @BeforeEach
    void setUp() {
        progTest = new ProgTest();
    }

    @Test
    public void givenDivisorIs1_thenReturnStartValue() throws Exception {
        Assertions.assertEquals(5, progTest.divide(5,1));
        Assertions.assertEquals(3, progTest.divide(3,1));
    }

    @Test
    public void givenEvenNumberDivideBy2_thenReturnHalf() throws Exception {
        Assertions.assertEquals(3, progTest.divide(6,2));
        Assertions.assertEquals(5, progTest.divide(10,2));
    }

    @Test
    public void givenRealResultDouble_thenReturnRoundedValue() throws Exception {
        Assertions.assertEquals(3, progTest.divide(5,2));
        Assertions.assertEquals(7, progTest.divide(20,3));
        Assertions.assertEquals(2, progTest.divide(11,5));
    }

    @Test
    public void givenDivideByZero_thenThrowException() throws Exception {
        Assertions.assertThrows(DivideByZeroException.class, () -> progTest.divide(5,0));
    }

    @Test
    public void givenNegativeDiveByPositve_thenReturnNegative() throws Exception {
        Assertions.assertEquals(-2, progTest.divide(-11,5));
    }

    @Test
    public void givenNegativeDiveByNegative_thenReturnPositve() throws Exception {
        Assertions.assertEquals(2, progTest.divide(-11,-5));
    }
}