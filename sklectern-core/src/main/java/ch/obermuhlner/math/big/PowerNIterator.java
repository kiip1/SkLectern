package ch.obermuhlner.math.big;

import java.math.BigDecimal;
import java.math.MathContext;

final class PowerNIterator {

	private final BigDecimal x;

	private final MathContext mathContext;

	private BigDecimal powerOfX;

	public PowerNIterator(BigDecimal x, MathContext mathContext) {
		this.x = x;
		this.mathContext = mathContext;

		powerOfX = BigDecimal.ONE;
	}

	public BigDecimal getCurrentPower() {
		return powerOfX;
	}

	public void calculateNextPower() {
		powerOfX = powerOfX.multiply(x, mathContext);
	}
}