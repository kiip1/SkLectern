package nl.kiipdevelopment.sklectern.lexer;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@ApiStatus.Internal
public record Token(TokenType type, String value, Spacing spacing) {
    public String spaced() {
        return spacing.apply(value);
    }

    @ApiStatus.Internal
    public enum Spacing {
        NONE(Function.identity()),
        LEFT(input -> " " + input);

        private final Function<String, String> applier;

        Spacing(Function<String, String> applier) {
            this.applier = applier;
        }

        public String apply(String input) {
            return applier.apply(input);
        }
    }
}
