package nl.kiipdevelopment.sklectern.lexer;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@ApiStatus.Experimental
public interface ScriptLexer {
	static @NotNull ScriptLexer of(String script) {
		return new ScriptLexerImpl(script);
	}
	
	@Contract("-> new")
	Instance instance();
	
	String script();

	int indentation();

	interface Instance extends Iterable<Token> {
		Token next();

		Token peek();

        Token peekBefore(TokenType type);

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
				public Token next() {
					return Instance.this.next();
				}
			};
		}
	}
}
