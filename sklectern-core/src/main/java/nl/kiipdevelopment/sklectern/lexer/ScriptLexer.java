package nl.kiipdevelopment.sklectern.lexer;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * The lexer tokenizes a script, with every line beginning with an indent token and ending with an end token.
 * When the lexer sees a character which has a {@link TokenType} representing it, it will return the current identifier
 * to then proceed with lexing the next special character.
 *
 * @see Token
 */
@ApiStatus.Experimental
public interface ScriptLexer {
	static @NotNull ScriptLexer of(@NotNull String script) {
		return new ScriptLexerImpl(script);
	}
	
	@Contract("-> new")
	@NotNull Instance instance();

    @NotNull String script();

	int indentation();

	interface Instance extends Iterable<Token> {
        @NotNull Token next();

        @NotNull Token peek();

        @NotNull Token peekBefore(TokenType type);

        boolean hasNext();

		int position();

		@Override
		default @NotNull Iterator<Token> iterator() {
			return new Iterator<>() {
				@Override
				public boolean hasNext() {
					return Instance.this.hasNext();
				}

				@Override
				public @NotNull Token next() {
					return Instance.this.next();
				}
			};
		}
	}
}
