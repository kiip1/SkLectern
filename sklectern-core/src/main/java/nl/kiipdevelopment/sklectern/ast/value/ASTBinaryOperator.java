package nl.kiipdevelopment.sklectern.ast.value;

import ch.obermuhlner.math.big.BigDecimalMath;
import nl.kiipdevelopment.sklectern.ast.ASTGroup;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.context.MathContext;
import nl.kiipdevelopment.sklectern.lexer.Token.Spacing;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BinaryOperator;
import java.util.function.Function;

@ApiStatus.Internal
public record ASTBinaryOperator<T>(ASTNode left, ASTNode right, TokenType operator, BinaryOperation operation, Spacing spacing) implements ASTValue<T> {
    @Override
    public @NotNull ASTNode shake() {
        return new ASTBinaryOperator<>(left.shake(), right.shake(), operator, operation, spacing);
    }

    @Override
    public T value(@NotNull Context context) {
        return (T) apply(context).value(context);
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

    public ASTValue<?> apply(@NotNull Context context) {
        return operation.apply(MathContext.of(context.copy(), left, right, operator, spacing));
    }

    public enum BinaryOperation {
        ADDITION(context -> operation(context, "addition", BigDecimal::add,
                (a, b) -> a.add(context, b))),
        SUBTRACTION(context -> operation(context, "subtraction", BigDecimal::subtract,
                (a, b) -> a.subtract(context, b))),
        MULTIPLICATION(context -> operation(context, "multiplication", BigDecimal::multiply,
                (a, b) -> a.multiply(context, b))),
        DIVISION(context -> operation(context, "division",
                (a, b) -> a.divide(b, RoundingMode.HALF_UP),
                (a, b) -> a.divide(context, b))),
        EXPONENTIATION(context -> {
            if (context.left() instanceof ASTNumber left && context.right() instanceof ASTNumber right)
                return new ASTNumber(BigDecimalMath.pow(left.value(context), right.value(context),
                        new java.math.MathContext(left.value(context).precision() +
                                right.value(context).precision(), RoundingMode.HALF_UP)));

            if (context.left() instanceof ASTLiteral<?> left && context.right() instanceof ASTLiteral<?> right)
                return new ASTString(left.visit(context) + " ^ " + right.visit(context));

            throw new ParseException("Attempted exponentiation on " + context.left() + " and " + context.right());
        });

        private final Function<MathContext, ASTValue<?>> binaryOperator;

        BinaryOperation(Function<MathContext, ASTValue<?>> binaryOperator) {
            this.binaryOperator = binaryOperator;
        }

        public @NotNull ASTValue<?> apply(@NotNull MathContext context) {
            if (context.left() instanceof ASTBinaryOperator<?> leftOperator)
                return apply(MathContext.of(context, leftOperator.apply(context), context.right(),
                        context.operator(), context.spacing()));

            if (context.right() instanceof ASTBinaryOperator<?> rightOperator)
                return apply(MathContext.of(context, context.left(), rightOperator.apply(context),
                        context.operator(), context.spacing()));

            if (context.left() instanceof ASTUnaryOperator<?> leftOperator)
                return apply(MathContext.of(context, leftOperator.apply(context), context.right(),
                        context.operator(), context.spacing()));

            if (context.right() instanceof ASTUnaryOperator<?> rightOperator)
                return apply(MathContext.of(context, context.left(), rightOperator.apply(context),
                        context.operator(), context.spacing()));

            return binaryOperator.apply(context);
        }

        private static @NotNull ASTValue<?> operation(
                @NotNull MathContext context, @NotNull String name,
                @NotNull BinaryOperator<BigDecimal> numberOperator, @NotNull BinaryOperator<ASTVector.Vector3D> vectorOperator
        ) {
            final Object left = context.left() instanceof ASTValue<?> value ? value.value(context) : null;
            final Object right = context.right() instanceof ASTValue<?> value ? value.value(context) : null;

            if (left != null && right != null)
                if (left instanceof ASTVector.Vector3D || right instanceof ASTVector.Vector3D) {
                    ASTVector.Vector3D leftVector = toVector(left);
                    ASTVector.Vector3D rightVector = toVector(right);
                    if (leftVector != null && rightVector != null)
                        return new ASTLiteralVector(vectorOperator.apply(leftVector, rightVector));
                } else if (left instanceof BigDecimal && right instanceof BigDecimal) {
                    if (context.operator().name().startsWith("VECTOR"))
                        throw new ParseException("Number math with vector operators is not supported");
                    return new ASTNumber(numberOperator.apply((BigDecimal) left, (BigDecimal) right));
                }

            final ASTString string = new ASTString(context.left().visit(context) +
                    context.spacing().apply(context.operator().value) +
                    context.right().visit(context));
            if (left instanceof String || right instanceof String) return string;
            else return new ASTGroup<>(string);
        }

        private static ASTVector.Vector3D toVector(Object object) {
            if (object instanceof BigDecimal decimal) return new ASTVector.Vector3D(decimal);
            else if (object instanceof ASTVector.Vector3D vector3D) return vector3D;
            else return null;
        }
    }
}
