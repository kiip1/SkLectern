package nl.kiipdevelopment.sklectern.ast.structure;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.ast.statement.ASTStatement;
import nl.kiipdevelopment.sklectern.ast.statement.ASTTransform;
import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@ApiStatus.Internal
public record ASTEntryTransform(ASTStructureEntry entry, Map<String, String> replacer) implements ASTStructureEntry {
    public ASTEntryTransform {
        replacer = Map.copyOf(replacer);
    }

    @Override
    public @NotNull ASTNode shake() {
        final ASTNode node = entry.shake();
        if (node instanceof ASTEmpty) return new ASTEmpty();
        else if (!(node instanceof ASTStructureEntry astEntry)) return new ASTEmpty();
        else return new ASTEntryTransform(astEntry, replacer);
    }

    @Override
    public void check(@NotNull Context context) {
        entry.check(context);
    }

    @Override
    public @Nullable ASTNode key() {
        final ASTNode key = entry.key();
        if (key == null) return null;
        else return transform(key);
    }

    @Override
    public @NotNull ASTNode node() {
        return transform(entry.node());
    }

    private @NotNull ASTNode transform(@NotNull ASTNode node) {
        if (node instanceof ASTStatement statement) return new ASTTransform(statement, replacer);
        else return new ASTNode() {
            @Override
            public @NotNull ASTNode shake() {
                return node.shake();
            }

            @Override
            public void check(@NotNull Context context) {
                node.check(context);
            }

            @Override
            public @NotNull String visit(@NotNull Context context) {
                String result = node.visit(context);
                for (Map.Entry<String, String> entry : replacer.entrySet())
                    result = result.replaceAll(entry.getKey(), entry.getValue());
                return result;
            }
        };
    }
}
