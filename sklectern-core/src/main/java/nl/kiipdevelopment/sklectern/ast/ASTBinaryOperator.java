package nl.kiipdevelopment.sklectern.ast;

import ch.obermuhlner.math.big.BigDecimalMath;
import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.BiFunction;

@ApiStatus.Internal
public record ASTBinaryOperator(ASTNode left, ASTNode right, BinaryOperator operator) implements ASTNode {
    @Override
    public void check(Context context) {
        left.check(context);
        right.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return apply().visit(context);
    }

    public ASTNode apply() {
        return operator.apply(left, right);
    }

    public enum BinaryOperator {
        CONCATENATE((left, right) -> new ASTString(left.visit(null) + " " + right.visit(null))),
        ADDITION((left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().add(rightNumber.value()));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " + " + rightLiteral.visit());

            throw new ParseException("Attempted addition on " + left + " and " + right);
        }),
        SUBTRACTION((left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().subtract(rightNumber.value()));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " - " + rightLiteral.visit());

            throw new ParseException("Attempted subtraction on " + left + " and " + right);
        }),
        MULTIPLICATION((left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().multiply(rightNumber.value()));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " * " + rightLiteral.visit());

            throw new ParseException("Attempted multiplication on " + left + " and " + right);
        }),
        DIVISION((left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().divide(rightNumber.value(), RoundingMode.HALF_UP));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " / " + rightLiteral.visit());

            throw new ParseException("Attempted division on " + left + " and " + right);
        }),
        EXPONENTIATION((left, right) -> {
            if (left instanceof ASTNumber leftNumber && right instanceof ASTNumber rightNumber)
                return new ASTNumber(BigDecimalMath.pow(leftNumber.value(), rightNumber.value(),
                        new MathContext(leftNumber.value().precision() +
                                rightNumber.value().precision(), RoundingMode.HALF_UP)));
            if (left instanceof ASTLiteral<?> leftLiteral && right instanceof ASTLiteral<?> rightLiteral)
                return new ASTString(leftLiteral.visit() + " ^ " + rightLiteral.visit());

            throw new ParseException("Attempted exponentiation on " + left + " and " + right);
        });

        private final BiFunction<ASTNode, ASTNode, ASTNode> applier;

        BinaryOperator(BiFunction<ASTNode, ASTNode, ASTNode> applier) {
            this.applier = applier;
        }

        public ASTNode apply(ASTNode left, ASTNode right) {
            if (left instanceof ASTBinaryOperator operator)
                return apply(operator.apply(), right);

            if (right instanceof ASTBinaryOperator operator)
                return apply(left, operator.apply());

            if (left instanceof ASTUnaryOperator operator)
                return apply(operator.apply(), right);

            if (right instanceof ASTUnaryOperator operator)
                return apply(left, operator.apply());

            return applier.apply(left, right);
        }
    }
}
