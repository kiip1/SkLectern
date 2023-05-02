package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.StructureMacro;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Context gets populated during the {@link ASTNode#check(Context)} phase,
 * and then used in the {@link ASTNode#visit(Context)} phase.
 * <p>
 * Note: This may become immutable later on depending on the direction {@link ASTNode} is heading in.
 */
@ApiStatus.Experimental
public sealed interface Context permits ContextImpl, MathContext {
    static @NotNull Context of() {
        return new ContextImpl();
    }

    @Unmodifiable Map<String, String> options();

    @Unmodifiable List<Macro> macros();

    @Unmodifiable List<StructureMacro> structureMacros();

    @Contract("_ -> this")
    Context options(Consumer<Map<String, String>> options);

    @Contract("_ -> this")
    Context macros(Consumer<List<Macro>> macros);

    @Contract("_ -> this")
    Context structureMacros(Consumer<List<StructureMacro>> macros);

    @Contract("-> new")
    Context copy();
}
