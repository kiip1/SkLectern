package nl.kiipdevelopment.sklectern.ast;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record ASTLiteralVector(Vector3D value) implements ASTVector, ASTLiteral<ASTVector.Vector3D> {
    public ASTLiteralVector(@NotNull ASTNode x, @NotNull ASTNode y, @NotNull ASTNode z) {
        this(new Vector3D(x, y, z));
    }
}
