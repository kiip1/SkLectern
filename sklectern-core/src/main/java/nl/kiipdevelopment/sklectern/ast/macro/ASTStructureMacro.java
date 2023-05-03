package nl.kiipdevelopment.sklectern.ast.macro;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructure;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructureEntry;
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
        final ASTNode node = structure.shake();
        if (node instanceof ASTEmpty) return new ASTEmpty();
        else if (!(node instanceof ASTStructure newStructure)) return new ASTEmpty();
        else return new ASTStructureMacro(name, arguments, newStructure);
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
    public @NotNull List<ASTStructureEntry> entries() {
        return List.of();
    }
}
