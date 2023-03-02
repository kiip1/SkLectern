package nl.kiipdevelopment.sklectern.ast;

import ch.obermuhlner.math.big.BigDecimalMath;
import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.MathContext;
import java.math.RoundingMode;

@ApiStatus.Internal
public record ASTBinaryOperator(ASTNode left, ASTNode right, BinaryOperator operator) implements ASTNode {
    @Override
    public void check(@NotNull Context context) {
        left.check(context);
        right.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return apply(context).visit(context);
    }

    public ASTNode apply(Context context) {
        return operator.apply(context, left, right);
    }

    public enum BinaryOperator {
        ADDITION((context, left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().add(rightNumber.value()));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " + " + rightLiteral.visit());

            throw new ParseException("Attempted addition on " + left + " and " + right);
        }),
        SUBTRACTION((context, left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().subtract(rightNumber.value()));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " - " + rightLiteral.visit());

            throw new ParseException("Attempted subtraction on " + left + " and " + right);
        }),
        MULTIPLICATION((context, left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().multiply(rightNumber.value()));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " * " + rightLiteral.visit());

            throw new ParseException("Attempted multiplication on " + left + " and " + right);
        }),
        DIVISION((context, left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().divide(rightNumber.value(), RoundingMode.HALF_UP));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " / " + rightLiteral.visit());

            throw new ParseException("Attempted division on " + left + " and " + right);
        }),
        EXPONENTIATION((context, left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(BigDecimalMath.pow(leftNumber.value(), rightNumber.value(),
                        new MathContext(leftNumber.value().precision() +
                                rightNumber.value().precision(), RoundingMode.HALF_UP)));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " ^ " + rightLiteral.visit());

            throw new ParseException("Attempted exponentiation on " + left + " and " + right);
        });

        private final ContextBinaryOperator<ASTNode> binaryOperator;

        BinaryOperator(ContextBinaryOperator<ASTNode> binaryOperator) {
            this.binaryOperator = binaryOperator;
        }

        public ASTNode apply(Context context, ASTNode left, ASTNode right) {
            if (left instanceof ASTBinaryOperator operator)
                return apply(context, operator.apply(context), right);

            if (right instanceof ASTBinaryOperator operator)
                return apply(context, left, operator.apply(context));

            if (left instanceof ASTUnaryOperator operator)
                return apply(context, operator.apply(context), right);

            if (right instanceof ASTUnaryOperator operator)
                return apply(context, left, operator.apply(context));

            return binaryOperator.apply(context, left, right);
        }

        @FunctionalInterface
        private interface ContextBinaryOperator<T> {
            @NotNull T apply(@NotNull Context context, @NotNull T left, @NotNull T right);
        }
    }
}
