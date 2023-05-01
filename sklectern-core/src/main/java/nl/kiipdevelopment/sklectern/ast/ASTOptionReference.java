package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@ApiStatus.Internal
public record ASTOptionReference(String name) implements ASTNode {
    @Override
    public @NotNull ASTNode shake() {
        return this;
    }

    @Override
    public void check(@NotNull Context context) {}

    @Override
    public @NotNull String visit(@NotNull Context context) {
        for (Map.Entry<String, String> option : context.options().entrySet())
            if (option.getKey().equals(name))
                return option.getValue();

        throw new ParseException("Reference to unknown option '" + name + "'");
    }
}
