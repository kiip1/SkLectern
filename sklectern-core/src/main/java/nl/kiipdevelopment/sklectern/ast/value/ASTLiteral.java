package nl.kiipdevelopment.sklectern.ast.value;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTLiteral<T> extends ASTValue<T> {
    T value();

    @Override
    default T value(@NotNull Context context) {
        return value();
    }
}
