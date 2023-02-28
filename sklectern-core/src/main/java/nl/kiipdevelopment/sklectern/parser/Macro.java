package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.ast.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public record Macro(String name, List<String> parameters, ASTStatementList statements) {
    public Macro {
        parameters = List.copyOf(parameters);
    }

    public @NotNull ASTStatement apply(@NotNull List<String> arguments) {
        final List<ASTStatement> results = new ArrayList<>();
        final Map<String, String> replacer = new HashMap<>();
        for (int i = 0; i < parameters.size(); i++)
            replacer.put("$" + parameters.get(i), arguments.get(i));
        for (ASTStatement statement : statements.statements())
            results.add(new ASTTransform(statement, replacer));

        return new ASTStatementList(results);
    }
}
