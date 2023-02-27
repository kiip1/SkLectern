package nl.kiipdevelopment.sklectern.lexer;

import com.google.common.collect.Queues;
import com.google.common.math.IntMath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.stream.Collectors;

record ScriptLexerImpl(String script) implements ScriptLexer {
	@Override
	public @NotNull Instance instance() {
		return new InstanceImpl(Queues.newArrayDeque(script
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

		private InstanceImpl(@NotNull Deque<Character> chars) {
			this.chars = chars;
			length = chars.size();
		}

		@Override
		public @NotNull Token next() {
			peek = null;
			Character next = chars.peek();
			if (next == null) {
				previous = TokenType.END;
				return new Token(TokenType.END, "");
			}
			if (next == '\n') {
				chars.poll();
				previous = TokenType.END;
				return new Token(TokenType.END, "");
			}

			if (previous == TokenType.END && (next == ' ' || next == '\t'))
				return indent();

			chars.poll();
			Character digitPeek = chars.peek();
			chars.addFirst(next);
			if (digitPeek != null && ((next == '-' && Character.isDigit(digitPeek)) || Character.isDigit(next))) {
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

			TokenType tokenType = TokenType.typeOfCharacter(next);
			if (tokenType != null) {
				chars.poll();
				previous = tokenType;
				return new Token(tokenType, next.toString());
			}

			previous = TokenType.IDENTIFIER;
			return identifier();
		}

		@Override
		public @NotNull Token peek() {
			if (peek != null)
				return peek;

			TokenType previous = this.previous;
			Deque<Character> chars = new ArrayDeque<>(this.chars);
			Token peek = next();
			this.chars.clear();
			this.chars.addAll(chars);
			this.peek = peek;
			this.previous = previous;

			return peek;
		}

        @Override
        public @NotNull Token peekBefore(TokenType type) {
            TokenType previous = this.previous;
            Token peek = this.peek;
            Deque<Character> chars = new ArrayDeque<>(this.chars);
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
			StringBuilder result = new StringBuilder();
			while (!chars.isEmpty() && (chars.peek() == ' ' || chars.peek() == '\t'))
				result.append(chars.poll());

			return new Token(TokenType.INDENT, result.toString());
		}

		private @NotNull Token number() {
			StringBuilder result = new StringBuilder();
			while (!chars.isEmpty() && (chars.peek() == '-' || Character.isDigit(chars.peek())))
				result.append(chars.poll());

			return new Token(TokenType.NUMBER, result.toString());
		}

		private @NotNull Token identifier() {
			StringBuilder result = new StringBuilder();
			do result.append(chars.poll());
			while (!chars.isEmpty() && chars.peek() != '\n' && TokenType.noAssociatedValue(chars.peek()));

			return new Token(TokenType.IDENTIFIER, result.toString());
		}

		private @NotNull Token string() {
			StringBuilder result = new StringBuilder();
			chars.poll();
			while (!chars.isEmpty() && chars.peek() != TokenType.QUOTE.value)
				result.append(chars.poll());
			chars.poll();

			return new Token(TokenType.STRING, "\"" + result + "\"");
		}

        private @NotNull Token variable() {
            StringBuilder result = new StringBuilder();
            chars.poll();
            while (!chars.isEmpty() && chars.peek() != TokenType.CURLY_CLOSE.value)
                result.append(chars.poll());
            chars.poll();

            return new Token(TokenType.VARIABLE, "{" + result + "}");
        }
	}
}
