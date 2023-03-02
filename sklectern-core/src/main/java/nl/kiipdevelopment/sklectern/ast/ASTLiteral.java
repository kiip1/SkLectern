package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.NotNull;

public interface ASTLiteral<T> extends ASTNode {
    @NotNull T value();

    default @NotNull String visit() {
        return value().toString();
    }

    @Override
    default @NotNull String visit(@NotNull Context context) {
        return visit();
    }
}
