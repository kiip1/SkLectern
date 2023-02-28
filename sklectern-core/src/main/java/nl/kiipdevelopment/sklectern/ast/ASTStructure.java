package nl.kiipdevelopment.sklectern.ast;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface ASTStructure extends ASTNode {
    String name();

    List<ASTStatement> entries();
}
