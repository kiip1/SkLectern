package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.lexer.Token.Spacing;
import nl.kiipdevelopment.sklectern.lexer.TokenType;
import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.StructureMacro;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

final class MathContextImpl implements MathContext {
    private final Context context;
    private final ASTNode left;
    private final ASTNode right;
    private final TokenType operator;
    private final Spacing spacing;

    public MathContextImpl(Context context, ASTNode left, ASTNode right, TokenType operator, Spacing spacing) {
        this.context = context;
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.spacing = spacing;
    }

    @Override
    public @NotNull ASTNode left() {
        return left;
    }

    @Override
    public @NotNull ASTNode right() {
        return right;
    }

    @Override
    public @NotNull TokenType operator() {
        return operator;
    }

    @Override
    public @NotNull Spacing spacing() {
        return spacing;
    }

    @Override
    public @Unmodifiable Map<String, String> options() {
        return context.options();
    }

    @Override
    public @Unmodifiable List<Macro> macros() {
        return context.macros();
    }

    @Override
    public @Unmodifiable List<StructureMacro> structureMacros() {
        return context.structureMacros();
    }

    @Override
    public Context options(Consumer<Map<String, String>> options) {
        return context.options(options);
    }

    @Override
    public Context macros(Consumer<List<Macro>> macros) {
        return context.macros(macros);
    }

    @Override
    public Context structureMacros(Consumer<List<StructureMacro>> macros) {
        return context.structureMacros(macros);
    }

    @Override
    public Context copy() {
        return context.copy();
    }
}
