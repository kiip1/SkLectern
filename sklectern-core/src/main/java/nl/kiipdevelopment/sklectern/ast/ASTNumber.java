package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;

import java.math.BigDecimal;

@ApiStatus.Internal
public record ASTNumber(BigDecimal value) implements ASTLiteral<BigDecimal> {
    @Override
    public void check(Context context) {}

    @Override
    public String visit() {
        return value.toString();
    }
}
