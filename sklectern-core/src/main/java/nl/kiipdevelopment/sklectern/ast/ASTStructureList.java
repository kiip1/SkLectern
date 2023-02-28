package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTStructureList(List<ASTStructure> structures) implements ASTNode {
    public ASTStructureList {
        structures = List.copyOf(structures);
    }

    @Override
    public void check(Context context) {
        for (ASTStructure structure : structures)
            structure.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        StringBuilder builder = new StringBuilder();
        for (ASTStructure structure : structures)
            builder.append(structure.visit(context)).append("\n");
        return builder.toString().replaceAll("(?m)^[ \\t]*\\r?\\n", "");
    }
}
