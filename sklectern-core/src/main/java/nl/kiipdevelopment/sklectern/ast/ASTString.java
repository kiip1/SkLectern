package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record ASTString(String value) implements ASTLiteral<String> {
    @Override
    public void check(Context context) {}

    @Override
    public String visit() {
        return value;
    }
}
