package nl.kiipdevelopment.sklectern.lexer;

import com.google.common.math.IntMath;
import nl.kiipdevelopment.sklectern.lexer.Token.Spacing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.stream.Collectors;

record ScriptLexerImpl(String script) implements ScriptLexer {
	@Override
	public @NotNull Instance instance() {
		return new InstanceImpl(new ArrayDeque<>(script
                .replaceAll("(?<!#)#(?!#).*", "")
                .replaceAll("(?m)^[ \\t]*\\r?\\n", "").chars()
				.mapToObj(integer -> (char) integer)
				.filter(character -> character != '\r')
				.collect(Collectors.toList())));
	}

	@Override
	public int indentation() {
		return Arrays.stream(script.split("\n"))
				.filter(line -> !line.replaceAll("^\\s+","").startsWith("#") && !line.isBlank())
				.mapToInt(line -> line.length() - line.replaceAll("^\\s+","").length())
				.reduce(IntMath::gcd)
				.orElse(0);
	}

	private static final class InstanceImpl implements Instance {

		private final @NotNull Deque<Character> chars;
		private final int length;

		@Nullable
		private Token peek;
		@Nullable
		private TokenType previous;
		private Spacing spacing;

		private InstanceImpl(@NotNull Deque<Character> chars) {
			this.chars = chars;
			length = chars.size();
		}

		@Override
		public @NotNull Token next() {
			return next(Spacing.NONE);
		}

		private @NotNull Token next(Spacing spacing) {
			this.spacing = spacing;
			peek = null;
            final Character next = chars.peek();
			if (next == null) {
				previous = TokenType.END;
				return new Token(TokenType.END, "", spacing);
			}
			if (next == '\n') {
				chars.poll();
				previous = TokenType.END;
				return new Token(TokenType.END, "", spacing);
			}

            if (next == ' ' && previous != TokenType.END) {
                chars.poll();
                return next(Spacing.LEFT);
            }

            if (previous == TokenType.END && (next == ' ' || next == '\t'))
				return indent();

			if (Character.isDigit(next)) {
				previous = TokenType.NUMBER;
				return number();
			}

			if (next == TokenType.QUOTE.value) {
				previous = TokenType.STRING;
				return string();
			}

            if (next == TokenType.CURLY_OPEN.value) {
                previous = TokenType.VARIABLE;
                return variable();
            }

            final TokenType tokenType = TokenType.typeOfCharacter(next);
			if (tokenType != null) {
				chars.poll();
				previous = tokenType;
				return new Token(tokenType, next.toString(), spacing);
			}

			previous = TokenType.IDENTIFIER;
			return identifier();
		}

		@Override
		public @NotNull Token peek() {
			if (peek != null)
				return peek;

            final TokenType previous = this.previous;
            final Deque<Character> chars = new ArrayDeque<>(this.chars);
            final Token peek = next();
			this.chars.clear();
			this.chars.addAll(chars);
			this.peek = peek;
			this.previous = previous;

			return peek;
		}

        @Override
        public @NotNull Token peekBefore(TokenType type) {
            final TokenType previous = this.previous;
            final Token peek = this.peek;
            final Deque<Character> chars = new ArrayDeque<>(this.chars);
            Token current = next();
            Token result = null;
            while (current.type() != type) {
                result = current;
                current = next();
            }
            this.chars.clear();
            this.chars.addAll(chars);
            this.previous = previous;
            this.peek = peek;

            return result == null ? current : result;
        }

		@Override
		public boolean hasNext() {
			return !chars.isEmpty();
		}

		@Override
		public int position() {
			return length - chars.size();
		}

		private @NotNull Token indent() {
			final StringBuilder result = new StringBuilder();
			while (!chars.isEmpty() && (chars.peek() == ' ' || chars.peek() == '\t'))
				result.append(chars.poll());

			return new Token(TokenType.INDENT, result.toString(), spacing);
		}

		private @NotNull Token number() {
			final StringBuilder result = new StringBuilder();
            boolean decimal = false;
            if (Objects.equals(chars.peek(), '-'))
                result.append(chars.poll());

			while (!chars.isEmpty()) {
                if (!decimal && chars.peek() == '.') {
                    decimal = true;
                    result.append(chars.poll());
                } else if (!Character.isDigit(chars.peek())) break;

                result.append(chars.poll());
            }

			return new Token(TokenType.NUMBER, result.toString(), spacing);
		}

		private @NotNull Token identifier() {
			final StringBuilder result = new StringBuilder();
			do result.append(chars.poll());
			while (!chars.isEmpty() && chars.peek() != '\n' && chars.peek() != ' ' && !Character.isDigit(chars.peek()) && TokenType.noAssociatedValue(chars.peek()));

			return new Token(TokenType.IDENTIFIER, result.toString(), spacing);
		}

		private @NotNull Token string() {
			final StringBuilder result = new StringBuilder();
			chars.poll();
			while (!chars.isEmpty() && chars.peek() != TokenType.QUOTE.value)
				result.append(chars.poll());
			chars.poll();

			return new Token(TokenType.STRING, "\"" + result + "\"", spacing);
		}

        // TODO Should probably be handled by the parser
        private @NotNull Token variable() {
            final StringBuilder result = new StringBuilder();
            int depth = 0;
            do {
                if (chars.peek() == TokenType.CURLY_OPEN.value) depth++;
                else if (chars.peek() == TokenType.CURLY_CLOSE.value) depth--;
                result.append(chars.poll());
            } while (!chars.isEmpty() && depth > 0);

            return new Token(TokenType.VARIABLE, result.toString(), spacing);
        }
	}
}
