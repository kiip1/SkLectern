package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.context.Config;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import nl.kiipdevelopment.sklectern.parser.ScriptParser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point to certain SkLectern features.
 *
 * @see ScriptLexer
 * @see ScriptParser
 * @see Script
 */
@ApiStatus.Experimental
public interface SkLectern {
    static @NotNull SkLectern instance() {
        return SkLecternImpl.instance();
    }

    static void setInstance(@NotNull SkLectern instance) {
        SkLecternImpl.instance = instance;
    }

    /**
     * @see Config
     */
    @NotNull Config config();

    /**
     * @see ScriptManager
     */
    @NotNull ScriptManager scriptManager();
}
