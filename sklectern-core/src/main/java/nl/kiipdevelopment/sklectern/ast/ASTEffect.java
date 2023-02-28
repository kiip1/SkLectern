package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public record ASTEffect(ASTNode node) implements ASTStatement {
    @Override
    public void check(Context context) {
        node.check(context);
    }

    @Override
    public List<String> get(Context context) {
        return List.of(node.visit(context));
    }
}
