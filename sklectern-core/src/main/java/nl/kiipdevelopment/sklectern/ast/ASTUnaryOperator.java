package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@ApiStatus.Internal
public record ASTUnaryOperator(ASTNode node, UnaryOperator operator) implements ASTNode {
    @Override
    public @NotNull ASTNode shake() {
        return new ASTUnaryOperator(node.shake(), operator);
    }

    @Override
    public void check(@NotNull Context context) {
        node.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return apply(context).visit(context);
    }

    public @NotNull ASTNode apply(@NotNull Context context) {
        return operator.apply(context, node);
    }

    public enum UnaryOperator {
        SUBTRACTION((context, input) -> {
            if (input instanceof ASTNumber number)
                return new ASTLiteralNumber(number.value(context).multiply(BigDecimal.valueOf(-1)));
            if (input instanceof ASTLiteral<?> literal)
                return new ASTString("-" + literal.visit(context));

            throw new ParseException("Attempted subtraction on " + input);
        });

        private final ContextUnaryOperator<ASTNode> unaryOperator;

        UnaryOperator(ContextUnaryOperator<ASTNode> unaryOperator) {
            this.unaryOperator = unaryOperator;
        }

        public @NotNull ASTNode apply(@NotNull Context context, @NotNull ASTNode input) {
            if (input instanceof ASTBinaryOperator<?> operator)
                return apply(context, operator.apply(context));

            if (input instanceof ASTUnaryOperator operator)
                return apply(context, operator.apply(context));

            return unaryOperator.apply(context, input);
        }

        @FunctionalInterface
        private interface ContextUnaryOperator<T> {
            @NotNull T apply(@NotNull Context context, @NotNull T input);
        }
    }
}
