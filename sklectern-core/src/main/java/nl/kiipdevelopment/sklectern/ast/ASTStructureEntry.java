package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public record ASTStructureEntry(ASTNode key, ASTNode node) implements ASTStatement {
    @Override
    public @NotNull ASTNode shake() {
        final ASTNode node = this.node.shake();
        if (node instanceof ASTEmpty) return new ASTEmpty();
        else return new ASTStructureEntry(key, node);
    }

    @Override
    public void check(@NotNull Context context) {
        if (key != null) key.check(context);
        node.check(context);
    }

    @Override
    public @NotNull List<String> get(@NotNull Context context) {
        final String key = this.key == null ? null : this.key.visit(context);
        final String prefix = key == null ? "" : key + ":";
        if (node instanceof ASTStatement statement) {
            final List<String> list = new ArrayList<>(statement.get(context)
                    .stream()
                    .map(line -> (key == null ? "" : "\t") + line).toList());
            if (key != null) list.add(prefix);

            return list;
        } else return List.of(prefix + node.visit(context));
    }
}
