package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import org.jetbrains.annotations.NotNull;

public sealed interface MathContext extends Context permits MathContextImpl {
    static MathContext of(Context context, ASTNode left, ASTNode right, TokenType operator) {
        return new MathContextImpl(context, left, right, operator);
    }

    @NotNull ASTNode left();

    @NotNull ASTNode right();

    @NotNull TokenType operator();
}
