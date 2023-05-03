package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This is the base for all AST elements, 
 */
@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTNode {
    /**
     * Recursively simplify this AST node, may change type in the process.
     */
    @Contract("-> new")
    @NotNull ASTNode shake();

    /**
     * Analyze this AST node and update context where needed.
     */
    void check(@NotNull Context context);

    /**
     * Turn the AST node into a string.
     */
    @NotNull String visit(@NotNull Context context);
}
