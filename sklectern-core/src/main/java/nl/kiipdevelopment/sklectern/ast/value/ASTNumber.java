package nl.kiipdevelopment.sklectern.ast.value;

import org.jetbrains.annotations.ApiStatus;

import java.math.BigDecimal;

@ApiStatus.Internal
public record ASTNumber(BigDecimal value) implements ASTLiteral<BigDecimal> {}
