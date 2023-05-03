package nl.kiipdevelopment.sklectern.ast;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTStructure extends ASTNode {
    @NotNull String name();

    @NotNull List<ASTStatement> entries();
}
