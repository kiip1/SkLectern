package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record ASTVariableReference(String name) implements ASTNode {
    @Override
    public @NotNull ASTNode shake() {
        return this;
    }

    @Override
    public void check(@NotNull Context context) {}

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return "{" + name + "}";
    }
}
