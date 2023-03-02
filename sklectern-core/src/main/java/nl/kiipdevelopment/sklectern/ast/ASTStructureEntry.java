package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

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
        node.check(context);
    }

    @Override
    public @NotNull List<String> get(@NotNull Context context) {
        final String key = this.key == null ? null : this.key.visit(context);
        final String prefix = key == null ? "" : key + ":";
        if (node instanceof ASTStatement statement) {
            return Stream.concat(Stream.of(prefix), statement.get(context)
                            .stream()
                            .map(line -> (key == null ? "" : "\t") + line)).toList();
        } else return List.of(prefix + node.visit(context));
    }
}
