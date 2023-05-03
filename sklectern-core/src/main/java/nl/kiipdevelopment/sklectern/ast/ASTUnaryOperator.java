package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.context.MathContext;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.Function;

@ApiStatus.Internal
public record ASTUnaryOperator<T>(ASTNode node, TokenType operator, UnaryOperation operation) implements ASTValue<T> {
    @Override
    public @NotNull ASTNode shake() {
        return new ASTUnaryOperator<>(node.shake(), operator, operation);
    }

    @Override
    public T value(@NotNull Context context) {
        return (T) apply(context).value(context);
    }

    @Override
    public void check(@NotNull Context context) {
        node.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return apply(context).visit(context);
    }

    public @NotNull ASTValue<?> apply(@NotNull Context context) {
        return operation.apply(context, node, operator);
    }

    public enum UnaryOperation {
        SUBTRACTION(context -> {
            final ASTNode input = context.left();

            if (input instanceof ASTVector vector)
                return new ASTLiteralVector(vector.value(context).multiply(context, new ASTVector.Vector3D(BigDecimal.valueOf(-1))));
            if (input instanceof ASTNumber number)
                return new ASTLiteralNumber(number.value(context).negate());
            if (input instanceof ASTLiteral<?> literal)
                return new ASTString("-" + literal.visit(context));

            throw new ParseException("Attempted subtraction on " + input);
        });

        private final Function<MathContext, ASTValue<?>> unaryOperator;

        UnaryOperation(Function<MathContext, ASTValue<?>> unaryOperator) {
            this.unaryOperator = unaryOperator;
        }

        public @NotNull ASTValue<?> apply(@NotNull Context context, @NotNull ASTNode input, @NotNull TokenType operator) {
            if (input instanceof ASTBinaryOperator<?> inputOperator)
                return apply(context, inputOperator.apply(context), operator);

            if (input instanceof ASTUnaryOperator<?> inputOperator)
                return apply(context, inputOperator.apply(context), operator);

            return unaryOperator.apply(MathContext.of(context.copy(), input, null, operator));
        }
    }
}
