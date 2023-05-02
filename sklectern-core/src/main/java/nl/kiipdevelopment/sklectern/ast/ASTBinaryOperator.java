package nl.kiipdevelopment.sklectern.ast;

import ch.obermuhlner.math.big.BigDecimalMath;
import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.context.MathContext;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.util.function.Function;

@ApiStatus.Internal
public record ASTBinaryOperator(ASTNode left, ASTNode right, TokenType operator, BinaryOperation operation) implements ASTNode {
    @Override
    public @NotNull ASTNode shake() {
        return new ASTBinaryOperator(left.shake(), right.shake(), operator, operation);
    }

    @Override
    public void check(@NotNull Context context) {
        left.check(context);
        right.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return apply(context).visit(context);
    }

    public ASTNode apply(@NotNull Context context) {
        return operation.apply(context, left, right, operator);
    }

    public enum BinaryOperation {
        ADDITION(context -> {
            if (context.left() instanceof ASTNumber leftNumber && context.right() instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().add(rightNumber.value()));
            if (((context.left() instanceof ASTVector || context.right() instanceof ASTVector) &&
                    (context.left() instanceof ASTNumber || context.right() instanceof ASTNumber)) ||
                    (context.left() instanceof ASTVector && context.right() instanceof ASTVector)) {

                final ASTVector left = context.left() instanceof ASTVector vector ? vector : ((ASTNumber) context.left()).vector();
                final ASTVector right = context.right() instanceof ASTVector vector ? vector : ((ASTNumber) context.right()).vector();
                return new ASTVector(left.value().add(right.value()));
            }
            if (context.left() instanceof ASTLiteral<?> left && context.right() instanceof ASTLiteral<?> right)
                return new ASTString(left.visit() + context.operator().value + right.visit());

            throw new ParseException("Attempted addition on " + context.left() + " and " + context.right());
        }),
        SUBTRACTION(context -> {
            if (context.left() instanceof ASTNumber leftNumber && context.right() instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().subtract(rightNumber.value()));
            if ((context.left() instanceof ASTVector || context.right() instanceof ASTVector) &&
                    (context.left() instanceof ASTNumber || context.right() instanceof ASTNumber)) {

                final ASTVector left = context.left() instanceof ASTVector vector ? vector : ((ASTNumber) context.left()).vector();
                final ASTVector right = context.right() instanceof ASTVector vector ? vector : ((ASTNumber) context.right()).vector();
                return new ASTVector(left.value().subtract(right.value()));
            }
            if (context.left() instanceof ASTLiteral<?> left && context.right() instanceof ASTLiteral<?> right)
                return new ASTString(left.visit() + context.operator().value + right.visit());

            throw new ParseException("Attempted subtraction on " + context.left() + " and " + context.right());
        }),
        MULTIPLICATION(context -> {
            if (context.left() instanceof ASTNumber leftNumber && context.right() instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().multiply(rightNumber.value()));
            if ((context.left() instanceof ASTVector || context.right() instanceof ASTVector) &&
                    (context.left() instanceof ASTNumber || context.right() instanceof ASTNumber)) {

                final ASTVector left = context.left() instanceof ASTVector vector ? vector : ((ASTNumber) context.left()).vector();
                final ASTVector right = context.right() instanceof ASTVector vector ? vector : ((ASTNumber) context.right()).vector();
                return new ASTVector(left.value().multiply(right.value()));
            }
            if (context.left() instanceof ASTLiteral<?> left && context.right() instanceof ASTLiteral<?> right)
                return new ASTString(left.visit() + context.operator().value + right.visit());

            throw new ParseException("Attempted multiplication on " + context.left() + " and " + context.right());
        }),
        DIVISION(context -> {
            if (context.left() instanceof ASTNumber leftNumber && context.right() instanceof ASTNumber rightNumber)
                return new ASTNumber(leftNumber.value().divide(rightNumber.value(), RoundingMode.HALF_UP));
            if ((context.left() instanceof ASTVector || context.right() instanceof ASTVector) &&
                    (context.left() instanceof ASTNumber || context.right() instanceof ASTNumber)) {

                final ASTVector left = context.left() instanceof ASTVector vector ? vector : ((ASTNumber) context.left()).vector();
                final ASTVector right = context.right() instanceof ASTVector vector ? vector : ((ASTNumber) context.right()).vector();
                return new ASTVector(left.value().divide(right.value()));
            }
            if (context.left() instanceof ASTLiteral<?> left && context.right() instanceof ASTLiteral<?> right)
                return new ASTString(left.visit() + context.operator().value + right.visit());

            throw new ParseException("Attempted division on " + context.left() + " and " + context.right());
        }),
        EXPONENTIATION(context -> {
            if (context.left() instanceof ASTNumber left && context.right() instanceof ASTNumber right)
                return new ASTNumber(BigDecimalMath.pow(left.value(), right.value(),
                        new java.math.MathContext(left.value().precision() +
                                right.value().precision(), RoundingMode.HALF_UP)));
            if (context.left() instanceof ASTLiteral<?> left && context.right() instanceof ASTLiteral<?> right)
                return new ASTString(left.visit() + " ^ " + right.visit());

            throw new ParseException("Attempted exponentiation on " + context.left() + " and " + context.right());
        });

        private final Function<MathContext<ASTNode>, ASTNode> binaryOperator;

        BinaryOperation(Function<MathContext<ASTNode>, ASTNode> binaryOperator) {
            this.binaryOperator = binaryOperator;
        }

        public @NotNull ASTNode apply(@NotNull Context context, @NotNull ASTNode left, @NotNull ASTNode right, @NotNull TokenType operator) {
            if (left instanceof ASTBinaryOperator leftOperator)
                return apply(context, leftOperator.apply(context), right, operator);

            if (right instanceof ASTBinaryOperator rightOperator)
                return apply(context, left, rightOperator.apply(context), operator);

            if (left instanceof ASTUnaryOperator leftOperator)
                return apply(context, leftOperator.apply(context), right, operator);

            if (right instanceof ASTUnaryOperator rightOperator)
                return apply(context, left, rightOperator.apply(context), operator);

            return binaryOperator.apply(MathContext.of(context.copy(), left, right, operator));
        }
    }
}
