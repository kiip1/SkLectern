package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.ast.*;
import nl.kiipdevelopment.sklectern.ast.ASTBinaryOperator.BinaryOperation;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import nl.kiipdevelopment.sklectern.lexer.Token;
import nl.kiipdevelopment.sklectern.lexer.Token.Spacing;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static nl.kiipdevelopment.sklectern.ast.ASTUnaryOperator.UnaryOperation;

record ScriptParserImpl(ScriptLexer lexer) implements ScriptParser {
    @Override
    public @NotNull Instance instance() {
        return new InstanceImpl(this, lexer.instance());
    }

    @Override
    public @NotNull String script() {
        return lexer.script();
    }

    @Override
    public int indentation() {
        return lexer.indentation();
    }

    private static final class InstanceImpl implements ScriptParser.Instance {
        private final @NotNull ScriptParser parser;
        private final ScriptLexer.Instance lexer;
        private final int indentationPerScopeLevel;
        private Token current = new Token(TokenType.END, "", Spacing.NONE);
        private Token previous = new Token(TokenType.END, "", Spacing.NONE);
        private int indentation;
        private boolean finished = false;

        private InstanceImpl(@NotNull ScriptParser parser, ScriptLexer.Instance lexer) {
            this.parser = parser;
            this.lexer = lexer;

            final int indentation = parser.indentation();
            this.indentationPerScopeLevel = indentation == 0 ? 4 : indentation;

            next();
        }

        @Override
        public @NotNull ASTNode parse() {
            List<ASTStructure> structures = new ArrayList<>();
            do structures.add(structure());
            while (lexer.hasNext());

            return new ASTStructureList(structures);
        }

        private @NotNull ASTStructure structure() {
            if (current.value().equalsIgnoreCase("structure") && peek().value().equalsIgnoreCase("macro"))
                return macro(true);
            else if (current.value().equalsIgnoreCase("macro"))
                return macro(false);
            else if (current.value().equalsIgnoreCase("options"))
                return options();
            else if (peek().type() == TokenType.MACRO) {
                final String name = current.value();
                eat(TokenType.IDENTIFIER);
                eat(TokenType.MACRO);
                eat(TokenType.PARENTHESIS_OPEN);
                final List<ASTNode> arguments = new ArrayList<>();
                while (previous.type() != TokenType.PARENTHESIS_CLOSE)
                    arguments.add(element(List.of(TokenType.COMMA, TokenType.PARENTHESIS_CLOSE)));
                eat(TokenType.END);

                return new ASTStructureMacroCall(name, new ASTNodeList(arguments));
            }

            final StringBuilder name = new StringBuilder();
            while (!(current.type() == TokenType.COLON && peek().type() == TokenType.END)) {
                name.append(current.spaced());
                next();
            }
            eat(TokenType.COLON);
            eat(TokenType.END);

            final int indentation = indent();
            // TODO Don't hardcode this
            if (name.toString().startsWith("command")) { // Structure
                List<ASTStatement> entries = new ArrayList<>();
                do entries.add(entry());
                while (this.indentation == indentation);

                return new ASTStruct(name.toString(), entries);
            } else { // Trigger
                return new ASTStruct(name.toString(), List.of(new ASTStructureEntry(null, statementList())));
            }
        }

        private @NotNull ASTStructure macro(boolean structure) {
            final StringBuilder builder = new StringBuilder();
            while (current.type() != TokenType.MACRO) {
                builder.append(current.value());
                next();
            }
            eat(TokenType.MACRO);
            eat(TokenType.PARENTHESIS_OPEN);
            final List<String> arguments = new ArrayList<>();
            while (current.type() != TokenType.PARENTHESIS_CLOSE) {
                arguments.add(current.value().trim());
                next();
                if (!ifEat(TokenType.COMMA))
                    break;
            }
            eat(TokenType.PARENTHESIS_CLOSE);
            eat(TokenType.COLON);
            eat(TokenType.END);

            final String name = builder.substring(structure ? 14 : 5);
            if (structure) return new ASTStructureMacro(name, arguments, structure());
            else return new ASTMacro(name, arguments, statementList());
        }

        private @NotNull ASTStructure options() {
            eat(TokenType.IDENTIFIER);
            eat(TokenType.COLON);
            eat(TokenType.END);
            List<ASTStatement> entries = new ArrayList<>();
            final int indentation = indent();
            do entries.add(entry());
            while (this.indentation >= indentation);

            return new ASTOptions(entries);
        }

        private @NotNull ASTStructureEntry entry() {
            final ASTNode key = element(List.of(TokenType.COLON, TokenType.END));
            if (previous.type() == TokenType.END)
                throw new ParseException(lexer, parser.script(), TokenType.COLON, TokenType.END);
            final boolean statementList = ifEat(TokenType.END);

            if (statementList) return new ASTStructureEntry(key, statementList());
            else return new ASTStructureEntry(key, element(List.of(TokenType.END)));
        }

        private @NotNull ASTStatementList statementList() {
            final int indentation = indent();
            if (indentation == 0) return new ASTStatementList(List.of());
            final List<ASTStatement> statements = new ArrayList<>();
            while (this.indentation == indentation)
                statements.add(statement());

            return new ASTStatementList(statements);
        }

        private @NotNull ASTStatement statement() {
            if (peek().type() == TokenType.MACRO) {
                final String name = current.value();
                eat(TokenType.IDENTIFIER);
                eat(TokenType.MACRO);
                eat(TokenType.PARENTHESIS_OPEN);
                final List<ASTNode> arguments = new ArrayList<>();
                while (previous.type() != TokenType.PARENTHESIS_CLOSE)
                    arguments.add(element(List.of(TokenType.COMMA, TokenType.PARENTHESIS_CLOSE)));
                eat(TokenType.END);

                return new ASTMacroCall(name, new ASTNodeList(arguments));
            }

            final ASTNode node = element(List.of(TokenType.END, TokenType.COLON));
            if (previous.type() == TokenType.COLON) {
                eat(TokenType.END);
                return new ASTSection(node, statementList());
            }
            return new ASTEffect(node);
        }

        private ASTNode factor() {
            if (current.type() == TokenType.PARENTHESIS_OPEN) {
                eat(TokenType.PARENTHESIS_OPEN);
                final ASTNode node = element(List.of(TokenType.PARENTHESIS_CLOSE));
                return new ASTGroup<>(node);
            } else if (current.type() == TokenType.MINUS && peek().spacing() == Spacing.NONE) {
                final Spacing spacing = current.spacing();
                eat(TokenType.MINUS);
                return new ASTUnaryOperator<>(factor(), TokenType.MINUS, UnaryOperation.SUBTRACTION, spacing);
            } else if (current.type() == TokenType.VARIABLE) {
                final String value = current.value();
                eat(TokenType.VARIABLE);

                if (!value.matches("\\{.+}"))
                    throw new ParseException(lexer, parser.script(), "Malformed variable");

                if (value.charAt(1) == '@') {
                    return new ASTOptionReference(value.substring(2, value.length() - 1));
                } else {
                    return new ASTVariableReference(value.substring(1, value.length() - 1));
                }
            }

            final Token current = this.current;
            next();
            if (current.type() == TokenType.NUMBER) return new ASTLiteralNumber(new BigDecimal(current.value()));
            else if (current.type() == TokenType.IDENTIFIER && this.current.type() == TokenType.PARENTHESIS_OPEN && this.current.spacing() == Spacing.NONE) {
                final String name = current.value();
                eat(TokenType.PARENTHESIS_OPEN);
                if (name.equals("vector")) {
                    final ASTNode x = element(List.of(TokenType.COMMA));
                    final ASTNode y = element(List.of(TokenType.COMMA));
                    final ASTNode z = element(List.of(TokenType.PARENTHESIS_CLOSE));
                    return new ASTLiteralVector(x, y, z);
                } else {
                    final List<ASTNode> arguments = new ArrayList<>();
                    while (previous.type() != TokenType.PARENTHESIS_CLOSE)
                        arguments.add(element(List.of(TokenType.COMMA, TokenType.PARENTHESIS_CLOSE)));
                    return new ASTFunctionCall(name, new ASTNodeList(arguments));
                }
            } else return new ASTString(current.value());
        }

        private ASTNode power() {
            ASTNode node = factor();

            while (current.type() == TokenType.EXPONENT) {
                // "spacing" can only be NONE (0), NONE --> LEFT
                int spacing = current.spacing().ordinal();

                eat(TokenType.EXPONENT);

                // "spacing" can either NONE (0) or LEFT (1), NONE --> RIGHT, LEFT --> BOTH
                spacing += current.spacing().ordinal() * 2;
                node = new ASTBinaryOperator<>(node, factor(), TokenType.EXPONENT, BinaryOperation.EXPONENTIATION, Spacing.values()[spacing]);
            }

            return node;
        }

        private ASTNode term() {
            ASTNode node = power();

            while (current.type() == TokenType.MULTIPLY || current.type() == TokenType.VECTOR_MULTIPLY || current.type() == TokenType.DIVIDE || current.type() == TokenType.VECTOR_DIVIDE) {
                final TokenType type;
                final BinaryOperation operator;

                // "spacing" can only be NONE (0), NONE --> LEFT
                int spacing = current.spacing().ordinal();

                if (current.type() == TokenType.MULTIPLY || current.type() == TokenType.VECTOR_MULTIPLY) {
                    type = eat(TokenType.MULTIPLY, TokenType.VECTOR_MULTIPLY);
                    operator = BinaryOperation.MULTIPLICATION;
                } else {
                    type = eat(TokenType.DIVIDE, TokenType.VECTOR_DIVIDE);
                    operator = BinaryOperation.DIVISION;
                }

                // "spacing" can either NONE (0) or LEFT (1), NONE --> RIGHT, LEFT --> BOTH
                spacing += current.spacing().ordinal() * 2;
                node = new ASTBinaryOperator<>(node, power(), type, operator, Spacing.values()[spacing]);
            }

            return node;
        }

        private ASTNode sum() {
            ASTNode node = term();

            while (current.type() == TokenType.PLUS || current.type() == TokenType.VECTOR_PLUS || current.type() == TokenType.MINUS || current.type() == TokenType.VECTOR_MINUS) {
                final TokenType type;
                final BinaryOperation operator;

                // "spacing" can only be NONE (0), NONE --> LEFT
                int spacing = current.spacing().ordinal();

                if (current.type() == TokenType.PLUS || current.type() == TokenType.VECTOR_PLUS) {
                    type = eat(TokenType.PLUS, TokenType.VECTOR_PLUS);
                    operator = BinaryOperation.ADDITION;
                } else {
                    type = eat(TokenType.MINUS, TokenType.VECTOR_MINUS);
                    operator = BinaryOperation.SUBTRACTION;
                }

                // "spacing" can either NONE (0) or LEFT (1), NONE --> RIGHT, LEFT --> BOTH
                spacing += current.spacing().ordinal() * 2;
                node = new ASTBinaryOperator<>(node, term(), type, operator, Spacing.values()[spacing]);
            }

            return node;
        }

        private @NotNull ASTNode element(@NotNull List<TokenType> closers) {
            final List<ASTNode> nodes = new ArrayList<>();
            while (!closers.contains(current.type())) {
                if (current.spacing() == Spacing.LEFT)
                    nodes.add(new ASTString(" "));

                nodes.add(sum());
            }
            if (ifEat(closers.toArray(TokenType[]::new)) == null)
                throw new ParseException(lexer, parser.script(), closers, current.type());

            return new ASTNodeList(nodes);
        }

        private int indent() {
            final int indentation = current.value().length() / indentationPerScopeLevel;
            if (current.type() == TokenType.END) this.indentation = 0;
            else if (ifEat(TokenType.INDENT)) this.indentation = indentation;

            return this.indentation;
        }

        private void next() {
            if (finished) throw new ParseException(lexer, parser.script(), "EOF reached");
            if (current.type() == TokenType.END && !lexer.hasNext()) finished = true;
            previous = current;
            current = lexer.next();
            indent();
        }

        private Token peek() {
            return lexer.peek();
        }

        private Token peekBefore(TokenType type) {
            return lexer.peekBefore(type);
        }

        private @NotNull TokenType eat(@NotNull TokenType type) {
            if (current.type() != type)
                throw new ParseException(lexer, parser.script(), type, current.type());
            next();

            return type;
        }

        private @NotNull TokenType eat(@NotNull TokenType @NotNull ... types) {
            final TokenType type = ifEat(types);
            if (type == null) throw new ParseException(lexer, parser.script(), types[0], current.type());
            return type;
        }

        private boolean ifEat(@NotNull TokenType type) {
            if (current.type() == type) {
                next();
                return true;
            }

            return false;
        }

        private @Nullable TokenType ifEat(@NotNull TokenType @NotNull ... types) {
            for (TokenType type : types)
                if (ifEat(type))
                    return type;

            return null;
        }
    }
}
