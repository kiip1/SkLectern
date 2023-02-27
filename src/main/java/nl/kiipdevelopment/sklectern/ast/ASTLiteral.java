package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record ASTLiteral(String value) implements ASTNode {
    @Override
    public void check(Context context) {}

    @Override
    public String visit(Context context) {
        return value;
    }
}
