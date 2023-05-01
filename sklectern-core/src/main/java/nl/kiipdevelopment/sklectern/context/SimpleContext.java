package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.StructureMacro;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;

final class SimpleContext implements Context {
    private final Map<String, String> options;
    private final List<Macro> macros;
    private final List<StructureMacro> structureMacros;

    public SimpleContext() {
        this(new LinkedHashMap<>(), new ArrayList<>(), new ArrayList<>());
    }

    public SimpleContext(Map<String, String> options, List<Macro> macros, List<StructureMacro> structureMacros) {
        this.options = options;
        this.macros = macros;
        this.structureMacros = structureMacros;
    }

    @Override
    public @Unmodifiable Map<String, String> options() {
        return Collections.unmodifiableMap(options);
    }

    @Override
    public @Unmodifiable List<Macro> macros() {
        return Collections.unmodifiableList(macros);
    }

    @Override
    public @Unmodifiable List<StructureMacro> structureMacros() {
        return structureMacros;
    }

    @Override
    public @NotNull Context options(Consumer<Map<String, String>> options) {
        options.accept(this.options);
        return this;
    }

    @Override
    public @NotNull Context macros(@NotNull Consumer<List<Macro>> macros) {
        macros.accept(this.macros);
        return this;
    }

    @Override
    public @NotNull Context structureMacros(Consumer<List<StructureMacro>> structureMacros) {
        structureMacros.accept(this.structureMacros);
        return this;
    }

    @Override
    public @NotNull Context copy() {
        return new SimpleContext(options, macros, structureMacros);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SimpleContext) obj;
        return Objects.equals(this.options, that.options) &&
                Objects.equals(this.macros, that.macros) &&
                Objects.equals(this.structureMacros, that.structureMacros);
    }

    @Override
    public int hashCode() {
        return Objects.hash(options, macros, structureMacros);
    }

    @Override
    public @NotNull String toString() {
        return "SimpleContext[" +
                "options=" + options + ',' +
                "macros=" + macros + ',' +
                "structureMacros=" + structureMacros + ']';
    }
}
