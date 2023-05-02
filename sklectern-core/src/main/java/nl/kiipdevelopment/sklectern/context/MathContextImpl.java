package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.lexer.TokenType;
import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.StructureMacro;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

final class MathContextImpl<T> implements MathContext<T> {
    private final Context context;
    private final T left;
    private final T right;
    private final TokenType operator;

    public MathContextImpl(Context context, T left, T right, TokenType operator) {
        this.context = context;
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public @NotNull T left() {
        return left;
    }

    @Override
    public @NotNull T right() {
        return right;
    }

    @Override
    public @NotNull TokenType operator() {
        return operator;
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
