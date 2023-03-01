package ch.obermuhlner.math.big;

import org.jetbrains.annotations.ApiStatus;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.BigDecimal.*;

/**
 * @see <a href="https://github.com/eobermuhlner/big-math">Source</a>
 * @author Eric Obermühlner
 */
@ApiStatus.Internal
public final class BigDecimalMath {
	private static final BigDecimal TWO = valueOf(2);
	private static final BigDecimal THREE = valueOf(3);

	private static final BigDecimal DOUBLE_MAX_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);

	private static volatile BigDecimal log2Cache;
	private static final Object log2CacheLock = new Object();

	private static volatile BigDecimal log3Cache;
	private static final Object log3CacheLock = new Object();

	private static volatile BigDecimal log10Cache;
	private static final Object log10CacheLock = new Object();

	private static final int EXPECTED_INITIAL_PRECISION = 15;

	private BigDecimalMath() {}

	public static boolean isDoubleValue(BigDecimal value) {
		if (value.compareTo(DOUBLE_MAX_VALUE) > 0) {
			return false;
		}
		return value.compareTo(DOUBLE_MAX_VALUE.negate()) >= 0;
	}

	public static BigDecimal mantissa(BigDecimal value) {
		int exponent = exponent(value);
		if (exponent == 0) {
			return value;
		}

		return value.movePointLeft(exponent);
	}

	public static int exponent(BigDecimal value) {
		return value.precision() - value.scale() - 1;
	}

	public static BigDecimal integralPart(BigDecimal value) {
		return value.setScale(0, RoundingMode.DOWN);
	}

	public static BigDecimal fractionalPart(BigDecimal value) {
		return value.subtract(integralPart(value));
	}

    public static BigDecimal round(BigDecimal value, MathContext mathContext) {
	    return value.round(mathContext);
    }

	public static BigDecimal reciprocal(BigDecimal x, MathContext mathContext) {
		return BigDecimal.ONE.divide(x, mathContext);
	}

	public static BigDecimal pow(BigDecimal x, BigDecimal y, MathContext mathContext) {
		checkMathContext(mathContext);
		if (x.signum() == 0) {
			switch (y.signum()) {
				case 0 -> {
					return round(ONE, mathContext);
				}
				case 1 -> {
					return round(ZERO, mathContext);
				}
			}
		}

		try {
			long longValue = y.longValueExact();
			return pow(x, longValue, mathContext);
		} catch (ArithmeticException ignored) {}

		if (fractionalPart(y).signum() == 0) {
			return powInteger(x, y, mathContext);
		}

		MathContext mc = new MathContext(mathContext.getPrecision() + 6, mathContext.getRoundingMode());
		BigDecimal result = exp(y.multiply(log(x, mc), mc), mc);

		return round(result, mathContext);
	}

	public static BigDecimal pow(BigDecimal x, long y, MathContext mathContext) {
		MathContext mc = mathContext.getPrecision() == 0 ? mathContext : new MathContext(mathContext.getPrecision() + 10, mathContext.getRoundingMode());

		if (y < 0) {
			BigDecimal value = reciprocal(pow(x, -y, mc), mc);
			return round(value, mathContext);
		}

		BigDecimal result = ONE;
		while (y > 0) {
			if ((y & 1) == 1) {

				result = result.multiply(x, mc);
				y -= 1;
			}

			if (y > 0) {

				x = x.multiply(x, mc);
			}

			y >>= 1;
		}

		return round(result, mathContext);
	}

	private static BigDecimal powInteger(BigDecimal x, BigDecimal integerY, MathContext mathContext) {
		if (fractionalPart(integerY).signum() != 0) {
			throw new IllegalArgumentException("Not integer value: " + integerY);
		}

		if (integerY.signum() < 0) {
			return ONE.divide(powInteger(x, integerY.negate(), mathContext), mathContext);
		}

		MathContext mc = new MathContext(Math.max(mathContext.getPrecision(), -integerY.scale()) + 30, mathContext.getRoundingMode());

		BigDecimal result = ONE;
		while (integerY.signum() > 0) {
			BigDecimal halfY = integerY.divide(TWO, mc);

			if (fractionalPart(halfY).signum() != 0) {

				result = result.multiply(x, mc);
				integerY = integerY.subtract(ONE);
				halfY = integerY.divide(TWO, mc);
			}

			if (halfY.signum() > 0) {

				x = x.multiply(x, mc);
			}

			integerY = halfY;
		}

		return round(result, mathContext);
	}

	public static BigDecimal log(BigDecimal x, MathContext mathContext) {
		checkMathContext(mathContext);
		if (x.signum() <= 0) {
			throw new ArithmeticException("Illegal log(x) for x <= 0: x = " + x);
		}
		if (x.compareTo(ONE) == 0) {
			return ZERO;
		}

		BigDecimal result = switch (x.compareTo(TEN)) {
			case 0 -> logTen(mathContext);
			case 1 -> logUsingExponent(x, mathContext);
			default -> logUsingTwoThree(x, mathContext);
		};

		return round(result, mathContext);
	}

	private static BigDecimal logUsingNewton(BigDecimal x, MathContext mathContext) {

		int maxPrecision = mathContext.getPrecision() + 20;
		BigDecimal acceptableError = ONE.movePointLeft(mathContext.getPrecision() + 1);

		BigDecimal result;
		int adaptivePrecision;
		double doubleX = x.doubleValue();
		if (doubleX > 0.0 && isDoubleValue(x)) {
			result = BigDecimal.valueOf(Math.log(doubleX));
			adaptivePrecision = EXPECTED_INITIAL_PRECISION;
		} else {
			result = x.divide(TWO, mathContext);
			adaptivePrecision = 1;
		}

		BigDecimal step;

		do {
			adaptivePrecision *= 3;
			if (adaptivePrecision > maxPrecision) {
				adaptivePrecision = maxPrecision;
			}
			MathContext mc = new MathContext(adaptivePrecision, mathContext.getRoundingMode());

			BigDecimal expY = BigDecimalMath.exp(result, mc);
			step = TWO.multiply(x.subtract(expY)).divide(x.add(expY), mc);

			result = result.add(step);
		} while (adaptivePrecision < maxPrecision || step.abs().compareTo(acceptableError) > 0);

		return result;
	}

	private static BigDecimal logUsingExponent(BigDecimal x, MathContext mathContext) {
		MathContext mcDouble = new MathContext(mathContext.getPrecision() << 1, mathContext.getRoundingMode());
        MathContext mc = new MathContext(mathContext.getPrecision() + 4, mathContext.getRoundingMode());

		int exponent = exponent(x);
		BigDecimal mantissa = mantissa(x);

		BigDecimal result = logUsingTwoThree(mantissa, mc);
		if (exponent != 0) {
			result = result.add(valueOf(exponent).multiply(logTen(mcDouble), mc));
		}
        return result;
	}

    @SuppressWarnings("StatementWithEmptyBody")
    private static BigDecimal logUsingTwoThree(BigDecimal x, MathContext mathContext) {
        MathContext mcDouble = new MathContext(mathContext.getPrecision() << 1, mathContext.getRoundingMode());
        MathContext mc = new MathContext(mathContext.getPrecision() + 4, mathContext.getRoundingMode());

        int factorOfTwo = 0;
        int powerOfTwo = 1;
        int factorOfThree = 0;
        int powerOfThree = 1;

        double value = x.doubleValue();
	    if (value < 0.01) {} else if (value < 0.1) {
            while (value < 0.6) {
                value *= 2;
                factorOfTwo--;
	            powerOfTwo <<= 1;
            }
        }
        else if (value < 0.115) {
            factorOfThree = -2;
            powerOfThree = 9;
        }
        else if (value < 0.14) {
            factorOfTwo = -3;
            powerOfTwo = 8;
        }
        else if (value < 0.2) {
            factorOfTwo = -1;
            powerOfTwo = 2;
            factorOfThree = -1;
            powerOfThree = 3;
        }
        else if (value < 0.3) {
            factorOfTwo = -2;
            powerOfTwo = 4;
        }
        else if (value < 0.42) {
            factorOfThree = -1;
            powerOfThree = 3;
        }
        else if (value < 0.7) {
            factorOfTwo = -1;
            powerOfTwo = 2;
        }
        else if (value < 1.4) {}
        else if (value < 2.5) {
            factorOfTwo = 1;
            powerOfTwo = 2;
        }
        else if (value < 3.5) {
            factorOfThree = 1;
            powerOfThree = 3;
        }
        else if (value < 5.0) {
            factorOfTwo = 2;
            powerOfTwo = 4;
        }
        else if (value < 7.0) {
            factorOfThree = 1;
            powerOfThree = 3;
            factorOfTwo = 1;
            powerOfTwo = 2;
        }
        else if (value < 8.5) {
            factorOfTwo = 3;
            powerOfTwo = 8;
        }
        else if (value < 10.0) {
            factorOfThree = 2;
            powerOfThree = 9;
        }
        else {
            while (value > 1.4) {
                value /= 2;
                factorOfTwo++;
	            powerOfTwo <<= 1;
            }
        }

        BigDecimal correctedX = x;
        BigDecimal result = ZERO;

        if (factorOfTwo > 0) {
            correctedX = correctedX.divide(valueOf(powerOfTwo), mc);
            result = result.add(logTwo(mcDouble).multiply(valueOf(factorOfTwo), mc));
        }
        else if (factorOfTwo < 0) {
            correctedX = correctedX.multiply(valueOf(powerOfTwo), mc);
            result = result.subtract(logTwo(mcDouble).multiply(valueOf(-factorOfTwo), mc));
        }

        if (factorOfThree > 0) {
            correctedX = correctedX.divide(valueOf(powerOfThree), mc);
            result = result.add(logThree(mcDouble).multiply(valueOf(factorOfThree), mc));
        }
        else if (factorOfThree < 0) {
            correctedX = correctedX.multiply(valueOf(powerOfThree), mc);
            result = result.subtract(logThree(mcDouble).multiply(valueOf(-factorOfThree), mc));
        }

        if (x.equals(correctedX) && result.equals(ZERO)) {
            return logUsingNewton(x, mathContext);
        }

        result = result.add(logUsingNewton(correctedX, mc), mc);

        return result;
    }

	private static BigDecimal logTen(MathContext mathContext) {
		BigDecimal result;

		synchronized (log10CacheLock) {
			if (log10Cache != null && mathContext.getPrecision() <= log10Cache.precision()) {
				result = log10Cache;
			} else {
				log10Cache = logUsingNewton(BigDecimal.TEN, mathContext);
				return log10Cache;
			}
		}

		return round(result, mathContext);
	}

	private static BigDecimal logTwo(MathContext mathContext) {
		BigDecimal result;

		synchronized (log2CacheLock) {
			if (log2Cache != null && mathContext.getPrecision() <= log2Cache.precision()) {
				result = log2Cache;
			} else {
				log2Cache = logUsingNewton(TWO, mathContext);
				return log2Cache;
			}
		}

		return round(result, mathContext);
	}

	private static BigDecimal logThree(MathContext mathContext) {
		BigDecimal result;

		synchronized (log3CacheLock) {
			if (log3Cache != null && mathContext.getPrecision() <= log3Cache.precision()) {
				result = log3Cache;
			} else {
				log3Cache = logUsingNewton(THREE, mathContext);
				return log3Cache;
			}
		}

		return round(result, mathContext);
	}

	public static BigDecimal exp(BigDecimal x, MathContext mathContext) {
		checkMathContext(mathContext);
		if (x.signum() == 0) {
			return ONE;
		}

		return expIntegralFractional(x, mathContext);
	}

	private static BigDecimal expIntegralFractional(BigDecimal x, MathContext mathContext) {
		BigDecimal integralPart = integralPart(x);

		if (integralPart.signum() == 0) {
			return expTaylor(x, mathContext);
		}

		BigDecimal fractionalPart = x.subtract(integralPart);

		MathContext mc = new MathContext(mathContext.getPrecision() + 10, mathContext.getRoundingMode());

        BigDecimal z = ONE.add(fractionalPart.divide(integralPart, mc));
        BigDecimal t = expTaylor(z, mc);

        BigDecimal result = pow(t, integralPart.intValueExact(), mc);

		return round(result, mathContext);
	}

	private static BigDecimal expTaylor(BigDecimal x, MathContext mathContext) {
		MathContext mc = new MathContext(mathContext.getPrecision() + 6, mathContext.getRoundingMode());

		x = x.divide(valueOf(256), mc);

		BigDecimal result = ExpCalculator.INSTANCE.calculate(x, mc);
		result = BigDecimalMath.pow(result, 256, mc);
		return round(result, mathContext);
	}

	private static void checkMathContext (MathContext mathContext) {
		if (mathContext.getPrecision() == 0) {
			throw new UnsupportedOperationException("Unlimited MathContext not supported");
		}
	}
}
