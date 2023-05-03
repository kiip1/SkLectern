package nl.kiipdevelopment.sklectern.ast.structure;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record ASTStructureEntryImpl(ASTNode key, ASTNode node) implements ASTStructureEntry {
    @Override
    public @NotNull ASTNode shake() {
        final ASTNode node = this.node.shake();
        if (node instanceof ASTEmpty) return new ASTEmpty();
        else return new ASTStructureEntryImpl(key, node);
    }
}
