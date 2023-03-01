package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
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
		// The line number doesn't work all the time
		this((script.substring(0, position - 1) +
                ">>>" + script.charAt(position - 1) + "<<<" +
                script.substring(position) +
                (message == null ? "" : " [" + message + ", line " + script.chars().limit(position).filter(x -> x == '\n').count() + "]")));
	}
	
	public ParseException(@NotNull String message) {
		super(((Function<String, String>) x -> {
			String[] parts = message.split("\n");
			return parts[parts.length - 1];
		}).apply(message), null);
	}
}
