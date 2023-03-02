package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record ASTString(String value) implements ASTLiteral<String> {
    @Override
    public void check(@NotNull Context context) {}

    @Override
    public @NotNull String visit() {
        return value;
    }
}
