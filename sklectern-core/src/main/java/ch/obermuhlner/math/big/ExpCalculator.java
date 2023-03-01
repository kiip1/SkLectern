package ch.obermuhlner.math.big;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

final class ExpCalculator {

	public static final ExpCalculator INSTANCE = new ExpCalculator();
	private final List<BigRational> factors = new ArrayList<>();

	private int n = 0;
	private BigRational oneOverFactorialOfN = BigRational.ONE;

	private BigRational getCurrentFactor() {
		return oneOverFactorialOfN;
	}

	private void calculateNextFactor() {
		n++;
		oneOverFactorialOfN = oneOverFactorialOfN.divide(n);
	}

	private PowerNIterator createPowerIterator(BigDecimal x, MathContext mathContext) {
		return new PowerNIterator(x, mathContext);
	}

	public BigDecimal calculate(BigDecimal x, MathContext mathContext) {
		BigDecimal acceptableError = BigDecimal.ONE.movePointLeft(mathContext.getPrecision() + 1);

		PowerNIterator powerIterator = createPowerIterator(x, mathContext);

		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal step;
		int i = 0;
		do {
			BigRational factor;
			BigDecimal xToThePower;

			factor = getFactor(i);
			xToThePower  = powerIterator.getCurrentPower();
			powerIterator.calculateNextPower();
			step = factor.getNumerator().multiply(xToThePower).divide(factor.getDenominator(), mathContext);
			i++;

			boolean calculateInPairs = false;
			if (calculateInPairs) {
				factor = getFactor(i);
				xToThePower = powerIterator.getCurrentPower();
				powerIterator.calculateNextPower();
				BigDecimal step2 = factor.getNumerator().multiply(xToThePower).divide(factor.getDenominator(), mathContext);
				step = step.add(step2);
				i++;
			}

			sum = sum.add(step);

		} while (step.abs().compareTo(acceptableError) > 0);

		return sum.round(mathContext);
	}

	private synchronized BigRational getFactor(int index) {
		while (factors.size() <= index) {
			BigRational factor = getCurrentFactor();
			addFactor(factor);
			calculateNextFactor();
		}
		return factors.get(index);
	}

	private void addFactor(BigRational factor){
		factors.add(requireNonNull(factor, "Factor cannot be null"));
	}

}
