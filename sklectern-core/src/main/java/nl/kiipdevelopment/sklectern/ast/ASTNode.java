package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface ASTNode {
    void check(@NotNull Context context);

    @NotNull String visit(@NotNull Context context);
}
