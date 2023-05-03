package nl.kiipdevelopment.sklectern.ast.option;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.ast.statement.ASTStatement;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructure;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructureEntry;
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
                .forEach(entry -> {
                    final String key = entry.key().visit(context);
                    final String value = entry.node().visit(context).trim();
                    if (key.isBlank()) throw new IllegalArgumentException("Option key is blank");
                    else if (value.isBlank()) throw new IllegalArgumentException("Option value is blank");
                    else if (options.put(key, value) != null) throw new IllegalStateException("Option '" + key + "' is already set");
                }));
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
