package nl.kiipdevelopment.sklectern.lexer;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A token type can either represent a character (like operators), a node (like strings and numbers) or
 * act as a marking, like indent and end.
 */
@ApiStatus.Internal
public enum TokenType {
    NUMBER,
    STRING,
    VARIABLE,
    IDENTIFIER,

    PLUS('+'),
    MINUS('-'),
    MULTIPLY('*'),
    DIVIDE('/'),
    EXPONENT('^'),

    PARENTHESIS_OPEN('('),
    PARENTHESIS_CLOSE(')'),
    CURLY_OPEN('{'),
    CURLY_CLOSE('}'),

    COLON(':'),
    COMMA(','),
    QUOTE('"'),
    MACRO('!'),

    INDENT,
    END;

    private static final Map<Character, TokenType> BY_VALUE = new HashMap<>();

    static {
        for (TokenType type : values())
            if (type.value != null)
                BY_VALUE.put(type.value, type);
    }

    @Nullable
    public final Character value;

    TokenType() {
        value = null;
    }

    TokenType(char value) {
        this.value = value;
    }

    @Override
    public @NotNull String toString() {
        if (value == null)
            return name().toLowerCase(Locale.ENGLISH);

        return value.toString();
    }

    public static @Nullable TokenType typeOfCharacter(char character) {
        return BY_VALUE.get(character);
    }

    public static boolean noAssociatedValue(char character) {
        return BY_VALUE.get(character) == null;
    }
}
