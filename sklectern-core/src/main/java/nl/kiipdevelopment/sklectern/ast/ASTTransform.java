package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public record ASTTransform(ASTStatement statement, Map<String, String> replacer) implements ASTStatement {
    public ASTTransform {
        replacer = Map.copyOf(replacer);
    }

    @Override
    public void check(@NotNull Context context) {
        statement.check(context);
    }

    @Override
    public @NotNull List<String> get(@NotNull Context context) {
        final List<String> result = new ArrayList<>(statement.get(context));
        for (Map.Entry<String, String> entry : replacer.entrySet())
            result.replaceAll(line -> line.replace(entry.getKey(), entry.getValue()));

        return result;
    }
}
