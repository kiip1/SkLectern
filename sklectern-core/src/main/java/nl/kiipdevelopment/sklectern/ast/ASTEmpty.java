package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.ast.statement.ASTStatement;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructure;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructureEntry;
import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTEmpty() implements ASTNode, ASTStatement, ASTStructure {
    @Override
    public @NotNull ASTNode shake() {
        return this;
    }

    @Override
    public void check(@NotNull Context context) {
        throw new UnsupportedOperationException("Can't check an empty AST node");
    }

    @Override
    public @NotNull List<String> get(@NotNull Context context) {
        throw new UnsupportedOperationException("Can't get an empty AST node");
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        throw new UnsupportedOperationException("Can't visit an empty AST node");
    }

    @Override
    public @NotNull String name() {
        throw new UnsupportedOperationException("Can't obtain name from an empty AST node");
    }

    @Override
    public @NotNull List<ASTStructureEntry> entries() {
        throw new UnsupportedOperationException("Can't obtain entries from an empty AST node");
    }
}
