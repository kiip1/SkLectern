package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.ast.*;
import nl.kiipdevelopment.sklectern.ast.ASTBinaryOperator.BinaryOperator;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import nl.kiipdevelopment.sklectern.lexer.Token;
import nl.kiipdevelopment.sklectern.lexer.Token.Spacing;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static nl.kiipdevelopment.sklectern.ast.ASTUnaryOperator.*;

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
            if (current.value().equals("structure") && peek().value().equals("macro"))
                return macro(true);
            else if (current.value().equals("macro"))
                return macro(false);
            else if (peek().type() == TokenType.MACRO) {
                final String name = current.value();
                eat(TokenType.IDENTIFIER);
                eat(TokenType.MACRO);
                eat(TokenType.PARENTHESIS_OPEN);
                final List<ASTNode> arguments = new ArrayList<>();
                while (previous.type() != TokenType.PARENTHESIS_CLOSE)
                    arguments.add(element(List.of(TokenType.COMMA, TokenType.PARENTHESIS_CLOSE)));
                eat(TokenType.END);

                return new ASTStructureMacroReference(name, new ASTNodeList(arguments));
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
            if ((name.toString().startsWith("command") || name.toString().equals("options")) && peek().type() == TokenType.COLON) { // Structure
                List<ASTStatement> entries = new ArrayList<>();
                do entries.add(entry());
                while (this.indentation >= indentation);

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

        private @NotNull ASTStructureEntry entry() {
            final ASTNode key = element(List.of(TokenType.COLON));
            final boolean statementList = ifEat(TokenType.END);

            if (statementList) return new ASTStructureEntry(key, statementList());
            else return new ASTStructureEntry(key, element(List.of(TokenType.END)));
        }

        private @NotNull ASTStatementList statementList() {
            final int indentation = indent();
            final List<ASTStatement> statements = new ArrayList<>();
            do statements.add(statement());
            while (this.indentation >= indentation);

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

                return new ASTMacroReference(name, new ASTNodeList(arguments));
            }

            final ASTNode node = element(List.of(TokenType.END, TokenType.COLON));
            if (previous.type() == TokenType.COLON) {
                eat(TokenType.END);
                return new ASTSection(node, statementList());
            }
            return new ASTEffect(node);
        }

        private ASTNode factor() {
            if (current.type() == TokenType.MINUS) {
                eat(TokenType.MINUS);
                return new ASTUnaryOperator(factor(), UnaryOperator.SUBTRACTION);
            }

            final Token current = this.current;
            next();
            if (current.type() == TokenType.NUMBER)
                return new ASTNumber(new BigDecimal(current.value()));
            return new ASTString(current.value());
        }

        private ASTNode power() {
            ASTNode node = factor();

            while (current.type() == TokenType.EXPONENT) {
                eat(TokenType.EXPONENT);
                node = new ASTBinaryOperator(node, factor(), BinaryOperator.EXPONENTIATION);
            }

            return node;
        }

        private ASTNode term() {
            ASTNode node = power();

            while (current.type() == TokenType.MULTIPLY || current.type() == TokenType.DIVIDE) {
                final BinaryOperator operator;
                if (current.type() == TokenType.MULTIPLY) {
                    eat(TokenType.MULTIPLY);
                    operator = BinaryOperator.MULTIPLICATION;
                } else {
                    eat(TokenType.DIVIDE);
                    operator = BinaryOperator.DIVISION;
                }

                node = new ASTBinaryOperator(node, power(), operator);
            }

            return node;
        }

        private ASTNode sum() {
            ASTNode node = term();

            while (current.type() == TokenType.PLUS || current.type() == TokenType.MINUS) {
                final BinaryOperator operator;
                if (current.type() == TokenType.PLUS) {
                    eat(TokenType.PLUS);
                    operator = BinaryOperator.ADDITION;
                } else {
                    eat(TokenType.MINUS);
                    operator = BinaryOperator.SUBTRACTION;
                }

                node = new ASTBinaryOperator(node, term(), operator);
            }

            return node;
        }

        private @NotNull ASTNode element(@NotNull List<TokenType> closers) {
            final List<ASTNode> nodes = new ArrayList<>();
            while (!closers.contains(current.type())) {
                if (current.type() == TokenType.NUMBER || current.type() == TokenType.PLUS || current.type() == TokenType.MINUS) {
                    if (current.spacing() == Spacing.LEFT)
                        nodes.add(new ASTString(" "));
                    nodes.add(sum());
                } else {
                    nodes.add(new ASTString(current.spaced()));
                    next();
                }
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

        private void eat(TokenType type) {
            if (current.type() != type)
                throw new ParseException(lexer, parser.script(), type, current.type());
            next();
        }

        private boolean ifEat(TokenType type) {
            if (current.type() == type) {
                next();
                return true;
            }

            return false;
        }

        private @Nullable TokenType ifEat(TokenType @NotNull ... types) {
            for (TokenType type : types)
                if (ifEat(type))
                    return type;

            return null;
        }

    }

}
