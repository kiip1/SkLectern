package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.ast.statement.ASTStatement;
import nl.kiipdevelopment.sklectern.ast.statement.ASTTransform;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStruct;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructure;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Like a {@link Macro} but for structures.
 *
 * @param name The macro name, without !
 * @param parameters Parameters this macro will accept
 * @param structure The content of this macro
 */
@ApiStatus.Internal
public record StructureMacro(String name, List<String> parameters, ASTStructure structure) {
    public StructureMacro {
        parameters = List.copyOf(parameters);
    }

    public @NotNull ASTStructure apply(@NotNull List<String> arguments) {
        final List<ASTStatement> results = new ArrayList<>();
        final Map<String, String> replacer = new HashMap<>();
        for (int i = 0; i < parameters.size(); i++)
            replacer.put("$" + parameters.get(i), arguments.get(i));
        for (ASTStatement entry : structure.entries())
            results.add(new ASTTransform(entry, replacer));

        String name = structure.name();
        for (Map.Entry<String, String> entry : replacer.entrySet())
            name = name.replace(entry.getKey(), entry.getValue());

        return new ASTStruct(name, results);
    }
}
