package nl.kiipdevelopment.sklectern.ast.structure;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTStructure extends ASTNode {
    @NotNull String name();

    @NotNull List<ASTStructureEntry> entries();
}
