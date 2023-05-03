package nl.kiipdevelopment.sklectern.ast.structure;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.ast.statement.ASTStatement;
import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTStructureEntry extends ASTStatement {
    @Nullable ASTNode key();

    @NotNull ASTNode node();

    @Override
    default void check(@NotNull Context context) {
        final ASTNode key = key();
        if (key != null) key.check(context);
        node().check(context);
    }

    @Override
    default @NotNull List<String> get(@NotNull Context context) {
        final ASTNode keyNode = key();
        final ASTNode node = node();
        final String key = keyNode == null ? null : keyNode.visit(context);
        final String prefix = key == null ? "" : key + ":";
        if (node instanceof ASTStatement statement) {
            final List<String> list = new ArrayList<>(statement.get(context)
                    .stream()
                    .map(line -> (key == null ? "" : "\t") + line).toList());
            if (key != null) list.add(0, prefix);

            return list;
        } else return List.of(prefix + node.visit(context));
    }
}
