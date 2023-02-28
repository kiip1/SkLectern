package nl.kiipdevelopment.sklectern.context;

import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.StructureMacro;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Experimental
public interface Context {
    static @NotNull Context of() {
        return new SimpleContext();
    }

    @Unmodifiable List<Macro> macros();

    @Unmodifiable List<StructureMacro> structureMacros();

    @Contract("_ -> this")
    Context macros(Consumer<List<Macro>> macros);

    @Contract("_ -> this")
    Context structureMacros(Consumer<List<StructureMacro>> macros);

    @Contract("-> new")
    Context copy();
}
