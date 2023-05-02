package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.lexer.TokenType;
import org.jetbrains.annotations.NotNull;

public sealed interface MathContext<T> extends Context permits MathContextImpl {
    static <T> MathContext<T> of(Context context, T left, T right, TokenType operator) {
        return new MathContextImpl<>(context, left, right, operator);
    }

    @NotNull T left();

    @NotNull T right();

    @NotNull TokenType operator();
}
