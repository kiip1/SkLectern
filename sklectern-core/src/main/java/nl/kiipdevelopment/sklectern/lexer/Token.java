package nl.kiipdevelopment.sklectern.lexer;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * A token represents a token type (string, number, quote) with the string value.
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
 * @param value The value
 * @param spacing The spacing
 */
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
