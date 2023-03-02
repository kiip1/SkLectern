package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.StructureMacro;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTStructureMacro(String name, List<String> arguments, ASTStructure structure) implements ASTStructure {
    public ASTStructureMacro {
        arguments = List.copyOf(arguments);
    }

    @Override
    public @NotNull ASTNode shake() {
        return new ASTStructureMacro(name, arguments, (ASTStructure) structure.shake());
    }

    @Override
    public void check(@NotNull Context context) {
        structure.check(context);
        context.structureMacros(macros -> macros.add(new StructureMacro(name, arguments, structure)));
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return "";
    }

    @Override
    public @NotNull List<ASTStatement> entries() {
        return List.of();
    }
}
