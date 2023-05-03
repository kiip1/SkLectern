package nl.kiipdevelopment.sklectern.ast.structure;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApiStatus.Internal
public record ASTStructureImpl(String name, List<ASTStructureEntry> entries) implements ASTStructure {
    public ASTStructureImpl {
        entries = List.copyOf(entries);
    }

    @Override
    public @NotNull ASTNode shake() {
        final List<ASTStructureEntry> entries = this.entries.stream()
                .map(ASTNode::shake)
                .filter(entry -> !(entry instanceof ASTEmpty))
                .map(entry -> (ASTStructureEntry) entry)
                .toList();
        if (entries.isEmpty()) return new ASTEmpty();
        else return new ASTStructureImpl(name, entries);
    }

    @Override
    public void check(@NotNull Context context) {
        for (ASTStructureEntry entry : entries)
            entry.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        List<String> results = new ArrayList<>();
        for (ASTStructureEntry entry : entries)
            results.addAll(entry.get(context));
        return name + ":\n" + results.stream().map(line -> "\t" + line).collect(Collectors.joining("\n"));
    }
}
