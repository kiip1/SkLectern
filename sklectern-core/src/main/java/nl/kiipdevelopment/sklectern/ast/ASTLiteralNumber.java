package nl.kiipdevelopment.sklectern.ast;

import org.jetbrains.annotations.ApiStatus;

import java.math.BigDecimal;

@ApiStatus.Internal
public record ASTLiteralNumber(BigDecimal value) implements ASTNumber, ASTLiteral<BigDecimal> {}
