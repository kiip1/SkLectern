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

        public @NotNull Spacing left() {
            return switch (this) {
                case NONE, LEFT -> LEFT;
                case RIGHT, BOTH -> BOTH;
            };
        }

        public @NotNull Spacing right() {
            return switch (this) {
                case NONE, RIGHT -> RIGHT;
                case LEFT, BOTH -> BOTH;
            };
        }
    }
}
