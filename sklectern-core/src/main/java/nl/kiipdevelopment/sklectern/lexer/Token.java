package nl.kiipdevelopment.sklectern.lexer;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A token represents a token type (string, number, quote) with the string node.
 * An example:
 * <pre>
 *     send 1 + 2
 *
 *     Token[IDENTIFIER, "send"]
 *     Token[NUMBER, "1"]
 *     Token[PLUS, "+"]
 *     Token[NUMBER, "2"]
 * </pre>
 *
 * The spacing indicates if there's a space to the left of this token.
 * This is required because Skript allows for custom addons which prevents
 * us from making an AST element for every syntax element, therefore we can't
 * detect every element and tell whether there needs to be a space or not.
 * Hence, we leave this up to the end-user.
 *
 * @param type The token type
 * @param value The node
 * @param spacing The spacing
 */
@ApiStatus.Internal
public record Token(@NotNull TokenType type, @NotNull String value, @NotNull Spacing spacing) {
    public @NotNull String spaced() {
        return spacing.apply(value);
    }

    @ApiStatus.Internal
    public enum Spacing {
        NONE(Function.identity()),
        LEFT(input -> " " + input),
        RIGHT(input -> input + " "),
        BOTH(input -> " " + input + " ");

        private final Function<String, String> applier;

        Spacing(Function<String, String> applier) {
            this.applier = applier;
        }

        public String apply(String input) {
            return applier.apply(input);
        }
    }
}
