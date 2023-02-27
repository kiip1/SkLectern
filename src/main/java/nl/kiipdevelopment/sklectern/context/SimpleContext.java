package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.StructureMacro;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

final class SimpleContext implements Context {
    private final List<Macro> macros;
    private final List<StructureMacro> structureMacros;

    public SimpleContext() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public SimpleContext(List<Macro> macros, List<StructureMacro> structureMacros) {
        this.macros = macros;
        this.structureMacros = structureMacros;
    }

    @Override
    public @Unmodifiable List<Macro> macros() {
        return macros;
    }

    @Override
    public @Unmodifiable List<StructureMacro> structureMacros() {
        return structureMacros;
    }

    @Override
    public @NotNull Context macros(@NotNull Consumer<List<Macro>> macros) {
        macros.accept(this.macros);
        return this;
    }

    @Override
    public Context structureMacros(Consumer<List<StructureMacro>> structureMacros) {
        structureMacros.accept(this.structureMacros);
        return this;
    }

    @Override
    public @NotNull Context copy() {
        return new SimpleContext(macros, structureMacros);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SimpleContext) obj;
        return Objects.equals(this.macros, that.macros) &&
                Objects.equals(this.structureMacros, that.structureMacros);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macros, structureMacros);
    }

    @Override
    public @NotNull String toString() {
        return "SimpleContext[" +
                "macros=" + macros + ',' +
                "structureMacros=" + structureMacros + ']';
    }
}
