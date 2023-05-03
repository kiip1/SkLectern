package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTValue<T> extends ASTNode {
    T value(@NotNull Context context);

    @Override
    default void check(@NotNull Context context) {}

    @Override
    default @NotNull ASTNode shake() {
        return this;
    }

    @Override
    default @NotNull String visit(@NotNull Context context) {
        return value(context).toString();
    }
}
