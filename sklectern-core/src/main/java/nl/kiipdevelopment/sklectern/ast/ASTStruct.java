package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApiStatus.Internal
public record ASTStruct(String name, List<ASTStatement> entries) implements ASTStructure {
    public ASTStruct {
        entries = List.copyOf(entries);
    }

    @Override
    public @NotNull ASTNode shake() {
        final List<ASTStatement> entries = this.entries.stream()
                .map(ASTNode::shake)
                .filter(entry -> !(entry instanceof ASTEmpty))
                .map(entry -> (ASTStatement) entry)
                .toList();
        if (entries.isEmpty()) return new ASTEmpty();
        else return new ASTStruct(name, entries);
    }

    @Override
    public void check(@NotNull Context context) {
        for (ASTStatement entry : entries)
            entry.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        List<String> results = new ArrayList<>();
        for (ASTStatement entry : entries)
            results.addAll(entry.get(context));
        return name + ":\n" + results.stream().map(line -> "\t" + line).collect(Collectors.joining("\n"));
    }
}
