package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTNodeList(List<ASTNode> nodes) implements ASTNode {
    public ASTNodeList {
        nodes = List.copyOf(nodes);
    }

    @Override
    public void check(Context context) {
        for (ASTNode node : nodes)
            node.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        StringBuilder builder = new StringBuilder();
        for (ASTNode node : nodes)
            builder.append(node.visit(context));
        return builder.toString();
    }
}
