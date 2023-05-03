package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A parse exception gets thrown when there is an error during the parsing of a script.
 */
@ApiStatus.Internal
public final class ParseException extends IllegalArgumentException {
	public ParseException(ScriptLexer.@NotNull Instance lexer, @NotNull String script, TokenType received) {
		this(lexer.position(), script, "Unexpected " + received);
	}
	
	public ParseException(ScriptLexer.@NotNull Instance lexer, @NotNull String script, TokenType expected, TokenType received) {
		this(lexer.position(), script, "Expected " + expected + " but got " + received);
	}

	public ParseException(ScriptLexer.@NotNull Instance lexer, @NotNull String script, @NotNull List<TokenType> expected, TokenType received) {
		this(lexer.position(), script, "Expected " + expected.stream()
				.map(TokenType::toString)
				.collect(Collectors.joining(", ")) + " but got " + received);
	}
	
	public ParseException(ScriptLexer.@NotNull Instance lexer, @NotNull String script, @Nullable String message) {
		this(lexer.position(), script, message);
	}
	
	public ParseException(int position, @NotNull String script, @Nullable String message) {
		this(transform(position, script, message));
	}
	
	public ParseException(@NotNull String message) {
		super(message, null);
	}

    private static @NotNull String transform(int position, @NotNull String script, @Nullable String message) {
        final StringBuilder builder = new StringBuilder();
        final String prefix = script.substring(0, position - 1);
        final String suffix = script.substring(position);
        builder.append(prefix.substring(prefix.lastIndexOf("\n") + 1));
        builder.append(">>>");
        builder.append(script.charAt(position - 1));
        builder.append("<<<");
        if (suffix.contains("\n")) builder.append(suffix, 0, suffix.indexOf("\n"));
        else builder.append(suffix);

        if (message != null) {
            final long line = script.substring(0, position)
                    .chars()
                    .filter(character -> character == '\n')
                    .count() + 1;
            builder.append(" [")
                    .append(message)
                    .append(", line ")
                    .append(line)
                    .append("]");
        }

        return builder.toString().replaceAll("\n", "");
    }
}
