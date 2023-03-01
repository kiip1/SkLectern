package ch.obermuhlner.math.big;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

final class BigRational extends Number implements Comparable<BigRational>, Serializable {

	public static final BigRational ZERO = new BigRational(0);

	public static final BigRational ONE = new BigRational(1);

	private final BigDecimal numerator;

	private final BigDecimal denominator;

	private BigRational(int value) {
		this(BigDecimal.valueOf(value), BigDecimal.ONE);
	}

	private BigRational(BigDecimal num, BigDecimal denominator) {
		BigDecimal n = num;
		BigDecimal d = denominator;

		if (d.signum() == 0) {
			throw new ArithmeticException("Divide by zero");
		}

		if (d.signum() < 0) {
			n = n.negate();
			d = d.negate();
		}

		numerator = n;
		this.denominator = d;
	}

	public BigDecimal getNumerator() {
		return numerator;
	}

	public BigDecimal getDenominator() {
		return denominator;
	}

	private BigRational divide(BigDecimal value) {
		BigDecimal d = denominator.multiply(value);
		return of(numerator, d);
	}

	public BigRational divide(BigInteger value) {
		if (value.equals(BigInteger.ONE)) {
			return this;
		}

		return divide(new BigDecimal(value));
	}

	public BigRational divide(int value) {
		return divide(BigInteger.valueOf(value));
	}

	public boolean isZero() {
		return numerator.signum() == 0;
	}

	private boolean isIntegerInternal() {
		return denominator.compareTo(BigDecimal.ONE) == 0;
	}

	private static int countDigits(BigInteger number) {
		double factor = Math.log(2) / Math.log(10);
		int digitCount = (int) (factor * number.bitLength() + 1);
		if (BigInteger.TEN.pow(digitCount - 1).compareTo(number) > 0) {
			return digitCount - 1;
		}
		return digitCount;
	}

	private int precision() {
		return countDigits(numerator.toBigInteger()) + countDigits(denominator.toBigInteger());
	}

	public double toDouble() {
		return toBigDecimal().doubleValue();
	}

	public float toFloat() {
		return toBigDecimal().floatValue();
	}

	public BigDecimal toBigDecimal() {
		int precision = Math.max(precision(), MathContext.DECIMAL128.getPrecision());
		return toBigDecimal(new MathContext(precision));
	}

	public BigDecimal toBigDecimal(MathContext mc) {
		return numerator.divide(denominator, mc);
	}

	@Override
	public int compareTo(@NotNull BigRational other) {
		if (this.equals(other)) {
			return 0;
		}
		return numerator.multiply(other.denominator).compareTo(denominator.multiply(other.numerator));
	}

	@Override
	public int hashCode() {
		if (isZero()) {
			return 0;
		}
		return numerator.hashCode() + denominator.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof BigRational other)) {
			return false;
		}

		if (!numerator.equals(other.numerator)) {
			return false;
		}
		return denominator.equals(other.denominator);
	}

	@Override
	public String toString() {
		if (isZero()) {
			return "0";
		}
		if (isIntegerInternal()) {
			return numerator.toString();
		}
		return toBigDecimal().toString();
	}

	private static BigRational of(BigDecimal numerator, BigDecimal denominator) {
		if (numerator.signum() == 0 && denominator.signum() != 0) {
			return ZERO;
		}
		if (numerator.compareTo(BigDecimal.ONE) == 0 && denominator.compareTo(BigDecimal.ONE) == 0) {
			return ONE;
		}
		return new BigRational(numerator, denominator);
	}

	@Override
	public int intValue() {
		return toBigDecimal().intValue();
	}

	@Override
	public long longValue() {
		return toBigDecimal().longValue();
	}

	@Override
	public float floatValue() {
		return toFloat();
	}

	@Override
	public double doubleValue() {
		return toDouble();
	}
}
