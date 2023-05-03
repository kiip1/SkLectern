package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.ast.structure.ASTEntryTransform;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructureImpl;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructure;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructureEntry;
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
        final List<ASTStructureEntry> results = new ArrayList<>();
        final Map<String, String> replacer = new HashMap<>();
        for (int i = 0; i < parameters.size(); i++)
            replacer.put("$" + parameters.get(i), arguments.get(i));
        for (ASTStructureEntry entry : structure.entries())
            results.add(new ASTEntryTransform(entry, replacer));

        String name = structure.name();
        for (Map.Entry<String, String> entry : replacer.entrySet())
            name = name.replace(entry.getKey(), entry.getValue());

        return new ASTStructureImpl(name, results);
    }
}
