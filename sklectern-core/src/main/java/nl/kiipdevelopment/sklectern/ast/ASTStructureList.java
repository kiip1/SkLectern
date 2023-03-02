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
    public @NotNull ASTNode shake() {
        final List<ASTStructure> structures = this.structures
                .stream()
                .map(ASTNode::shake)
                .filter(node -> !(node instanceof ASTEmpty))
                .filter(node -> node instanceof ASTStructure)
                .map(node -> (ASTStructure) node)
                .toList();

        if (structures.size() == 0) return new ASTEmpty();
        else if (structures.size() == 1) return structures.get(0);
        else return new ASTStructureList(structures);
    }

    @Override
    public void check(@NotNull Context context) {
        for (ASTStructure structure : structures)
            structure.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        final StringBuilder builder = new StringBuilder();
        for (ASTStructure structure : structures)
            builder.append(structure.visit(context)).append("\n");
        return builder.toString().replaceAll("(?m)^[ \\t]*\\r?\\n", "");
    }
}
