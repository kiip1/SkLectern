package nl.kiipdevelopment.sklectern.ast;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ASTNumber extends ASTValue<BigDecimal> {
    default @NotNull ASTVector vector() {
        return new ASTLiteralVector(this, this, this);
    }
}
