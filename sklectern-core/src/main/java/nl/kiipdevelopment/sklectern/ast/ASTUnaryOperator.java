package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.Function;

@ApiStatus.Internal
public record ASTUnaryOperator(ASTNode node, UnaryOperator operator) implements ASTNode {
    @Override
    public void check(Context context) {
        node.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return apply().visit(context);
    }

    public ASTNode apply() {
        return operator.apply(node);
    }

    public enum UnaryOperator {
        ADDITION(node -> {
            if (node instanceof ASTNumber number)
                return new ASTNumber(number.value());
            if (node instanceof ASTLiteral<?> literal)
                return new ASTString("+" + literal.visit());

            throw new ParseException("Attempted addition on " + node);
        }),
        SUBTRACTION(node -> {
            if (node instanceof ASTNumber number)
                return new ASTNumber(number.value().multiply(BigDecimal.valueOf(-1)));
            if (node instanceof ASTLiteral<?> literal)
                return new ASTString("-" + literal.visit());

            throw new ParseException("Attempted addition on " + node);
        });

        private final Function<ASTNode, ASTNode> applier;

        UnaryOperator(Function<ASTNode, ASTNode> applier) {
            this.applier = applier;
        }

        public ASTNode apply(ASTNode node) {
            if (node instanceof ASTBinaryOperator operator)
                return apply(operator.apply());

            if (node instanceof ASTUnaryOperator operator)
                return apply(operator.apply());

            return applier.apply(node);
        }
    }
}
