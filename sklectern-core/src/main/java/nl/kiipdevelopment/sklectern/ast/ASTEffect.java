package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTEffect(ASTNode node) implements ASTStatement {
    @Override
    public @NotNull ASTNode shake() {
        return new ASTEffect(node.shake());
    }

    @Override
    public void check(@NotNull Context context) {
        node.check(context);
    }

    @Override
    public @NotNull List<String> get(@NotNull Context context) {
        return List.of(node.visit(context));
    }
}
