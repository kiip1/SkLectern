package nl.kiipdevelopment.sklectern.lexer;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record Token(TokenType type, String value) {}
