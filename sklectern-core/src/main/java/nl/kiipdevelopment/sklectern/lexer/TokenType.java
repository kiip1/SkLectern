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

    PLUS('+', true),
    MINUS('-', true),
    MULTIPLY('*', true),
    DIVIDE('/', true),
    EXPONENT('^'),

    VECTOR_PLUS("++"),
    VECTOR_MINUS("--"),
    VECTOR_MULTIPLY("**"),
    VECTOR_DIVIDE("//"),

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

    private static final Map<String, TokenType> BY_VALUE = new HashMap<>();

    static {
        for (TokenType type : values())
            if (type.value != null)
                BY_VALUE.put(type.value, type);
    }

    public final @Nullable String value;
    public final boolean checkDouble;

    TokenType() {
        value = null;
        checkDouble = false;
    }

    TokenType(char value) {
        this(value, false);
    }

    TokenType(char value, boolean checkDouble) {
        this(String.valueOf(value), checkDouble);
    }

    TokenType(@NotNull String value) {
        this(value, false);
    }

    TokenType(@NotNull String value, boolean checkDouble) {
        this.value = value;
        this.checkDouble = checkDouble;
    }

    @Override
    public @NotNull String toString() {
        if (value == null)
            return name().toLowerCase(Locale.ENGLISH);
        return value;
    }

    public static @Nullable TokenType typeOfCharacter(String character) {
        return BY_VALUE.get(character);
    }

    public static boolean noAssociatedValue(String character) {
        return BY_VALUE.get(character) == null;
    }
}
