package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTOptions(List<ASTStatement> entries) implements ASTStructure {
    public ASTOptions {
        entries = List.copyOf(entries);
    }

    @Override
    public @NotNull ASTNode shake() {
        return new ASTOptions(entries.stream()
                .map(ASTNode::shake)
                .filter(entry -> !(entry instanceof ASTEmpty))
                .map(entry -> (ASTStatement) entry)
                .toList());
    }

    @Override
    public void check(@NotNull Context context) {
        for (ASTStatement entry : entries)
            entry.check(context);
        context.options(options -> entries.stream()
                .map(entry -> (ASTStructureEntry) entry)
                .forEach(entry -> options.put(entry.key().visit(context), entry.node().visit(context).trim())));
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return "";
    }

    @Override
    public @NotNull String name() {
        return "options";
    }
}
