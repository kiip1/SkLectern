package nl.kiipdevelopment.sklectern.parser;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructureList;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The parser builds an ast from tokens, with {@link ASTStructureList} as root.
 *
 * @see ASTNode
 */
@ApiStatus.Experimental
public interface ScriptParser {
    static @NotNull ScriptParser of(@NotNull ScriptLexer lexer) {
        return new ScriptParserImpl(lexer);
    }

    @Contract("-> new")
    @NotNull Instance instance();

    @NotNull String script();

    int indentation();

    interface Instance {
        @NotNull ASTNode parse();
    }
}
