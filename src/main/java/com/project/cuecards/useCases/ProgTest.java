package com.project.cuecards.useCases;

import com.project.cuecards.exceptions.DivideByZeroException;

public class ProgTest {

    public int divide(int startValue, int divisor) throws DivideByZeroException {
        if (isIllegalValue(divisor))
            throw new DivideByZeroException();
        return getRoundedCalculatedValue(startValue, divisor);
    }

    private boolean isIllegalValue(int divisor) {
        return divisor == 0;
    }

    private int getRoundedCalculatedValue(int startValue, int divisor) {
        double result = 1.0 * startValue / divisor;
        return (int) Math.round(result);
    }
}
