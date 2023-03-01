package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;

public interface ASTLiteral<T> extends ASTNode {
    T value();

    default String visit() {
        return value().toString();
    }

    @Override
    default String visit(Context context) {
        return visit();
    }
}
