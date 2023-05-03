package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.lexer.Token.Spacing;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public sealed interface MathContext extends Context permits MathContextImpl {
    static @NotNull MathContext of(
            @NotNull Context context, @NotNull ASTNode left, @Nullable ASTNode right,
            @NotNull TokenType operator, @NotNull Spacing spacing
    ) {
        return new MathContextImpl(context, left, right, operator, spacing);
    }

    @NotNull ASTNode left();

    @NotNull ASTNode right();

    @NotNull TokenType operator();

    @NotNull Spacing spacing();
}
