package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ASTStatement extends ASTNode {
    @NotNull List<String> get(@NotNull Context context);

    @Override
    default @NotNull String visit(@NotNull Context context) {
        return String.join("\n", get(context));
    }
}
