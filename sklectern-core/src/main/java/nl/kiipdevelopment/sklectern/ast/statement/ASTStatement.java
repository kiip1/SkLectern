package nl.kiipdevelopment.sklectern.ast.statement;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTStatement extends ASTNode {
    @NotNull List<String> get(@NotNull Context context);

    @Override
    default @NotNull String visit(@NotNull Context context) {
        return String.join("\n", get(context));
    }
}
