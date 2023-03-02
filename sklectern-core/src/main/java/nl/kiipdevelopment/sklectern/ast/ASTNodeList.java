package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;

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
    public String visit(Context context) {
        final StringBuilder result = new StringBuilder();
        for (ASTNode node : nodes)
            result.append(node.visit(context));
        return result.toString();
    }
}
