package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@ApiStatus.Internal
public record ASTNumber(BigDecimal value) implements ASTLiteral<BigDecimal> {
    @Override
    public void check(@NotNull Context context) {}

    @Override
    public @NotNull String visit() {
        return value.toString();
    }
}
