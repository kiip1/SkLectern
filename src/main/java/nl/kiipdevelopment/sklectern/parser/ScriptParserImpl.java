package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.ast.*;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import nl.kiipdevelopment.sklectern.lexer.Token;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

record ScriptParserImpl(ScriptLexer lexer) implements ScriptParser {
    @Override
    public ScriptParser.@NotNull Instance instance() {
        return new InstanceImpl(this, lexer.instance());
    }

    @Override
    public String script() {
        return lexer.script();
    }

    @Override
    public int indentation() {
        return lexer.indentation();
    }

    private static final class InstanceImpl implements ScriptParser.Instance {
        private final @NotNull ScriptParser parser;
        private final ScriptLexer.Instance lexer;
        private final int indentation;
        private Token current = new Token(TokenType.END, "");
        private Token previous = new Token(TokenType.END, "");
        private int indent;
        private boolean finished = false;

        private InstanceImpl(@NotNull ScriptParser parser, ScriptLexer.Instance lexer) {
            this.parser = parser;
            this.lexer = lexer;

            final int indentation = parser.indentation();
            this.indentation = indentation == 0 ? 4 : indentation;

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
            int offset = indent();
            if (current.value().startsWith("structure macro"))
                return macro(true);
            else if (current.value().startsWith("macro"))
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

                return new ASTStructureMacroReference(name, arguments);
            }

            StringBuilder builder = new StringBuilder();
            while (!(current.type() == TokenType.COLON && peek().type() == TokenType.END)) {
                builder.append(current.value());
                next();
            }
            eat(TokenType.COLON);
            eat(TokenType.END);

            if (builder.toString().startsWith("command") && peek().type() == TokenType.COLON) { // Structure
                List<ASTStatement> entries = new ArrayList<>();
                do entries.add(entry());
                while (indent > 0);

                return new ASTStruct(builder.toString(), entries);
            } else { // Trigger
                return new ASTStruct(builder.toString(), List.of(new ASTStructureEntry<>(offset, null, statementList())));
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

            final String name = builder.substring(structure ? 16 : 6);
            if (structure) return new ASTStructureMacro(name, arguments, structure());
            else return new ASTMacro(name, arguments, statementList());
        }

        private @NotNull ASTStructureEntry<?> entry() {
            int offset = indent();
            String key = current.value();
            eat(TokenType.IDENTIFIER);
            eat(TokenType.COLON);
            ASTNode node = element(List.of(TokenType.END));

            if (key.equals("trigger"))
                return new ASTStructureEntry<>(offset, key, statementList());
            return new ASTStructureEntry<>(offset, key, node);
        }

        private @NotNull ASTStatementList statementList() {
            final int offset = indent();
            final List<ASTStatement> statements = new ArrayList<>();
            do statements.add(statement());
            while (this.indent >= offset);

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

                return new ASTMacroReference(name, arguments);
            }

            final ASTNode node = element(List.of(TokenType.END, TokenType.COLON));
            if (previous.type() == TokenType.COLON) {
                eat(TokenType.END);
                return new ASTSection(node, statementList());
            }
            return new ASTEffect(node);
        }

        private @NotNull ASTNode element(@NotNull List<TokenType> closers) {
            final List<ASTNode> nodes = new ArrayList<>();
            while (!closers.contains(current.type())) {
                nodes.add(new ASTLiteral(current.value()));
                next();
            }
            if (ifEat(closers.toArray(TokenType[]::new)) == null)
                throw new ParseException(lexer, parser.script(), closers, current.type());

            return new ASTNodeList(nodes);
        }

        private int indent() {
            int indent = current.value().length() / indentation;
            if (current.type() == TokenType.END) this.indent = 0;
            else if (ifEat(TokenType.INDENT)) this.indent = indent;

            return this.indent;
        }

        private void next() {
            if (finished) throw new ParseException("EOF reached");
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
