package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;

import java.util.List;

public interface ASTStatement extends ASTNode {
    List<String> get(Context context);

    @Override
    default String visit(Context context) {
        return String.join("\n", get(context));
    }
}
