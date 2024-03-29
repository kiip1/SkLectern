package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.lexer.ScriptLexer;
import nl.kiipdevelopment.sklectern.parser.ScriptParser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.io.Files.getNameWithoutExtension;

/**
 * A utility class to improve Script-handling accessibility.
 *
 * @param name The name of the script
 * @param source Content of the script
 */
@ApiStatus.Experimental
public record Script(String name, String source) {
    public Script(@NotNull Path path) throws IOException {
        this(getNameWithoutExtension(path.getFileName().toString()), Files.readString(path));
    }

    public ASTNode parse() {
        if (source.isBlank()) return new ASTEmpty();

        final ScriptLexer lexer = ScriptLexer.of(source);
        final ScriptParser parser = ScriptParser.of(lexer);
        return parser.instance().parse().shake();
    }

    public String transform() {
        final ASTNode node = parse();
        final Context context = Context.of();
        node.check(context);
        return node.visit(context);
    }
}
