package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record ASTGroup<T>(ASTNode node) implements ASTValue<T>, ASTNode {
    @Override
    public T value(@NotNull Context context) {
        return node instanceof ASTValue<?> value ? (T) value.value(context) : null;
    }

    @Override
    public @NotNull ASTNode shake() {
        final ASTNode node = this.node.shake();
        if (node instanceof ASTEmpty) return new ASTEmpty();
        return new ASTGroup<>(node);
    }

    @Override
    public void check(@NotNull Context context) {
        node.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return "(" + node.visit(context) + ")";
    }
}
