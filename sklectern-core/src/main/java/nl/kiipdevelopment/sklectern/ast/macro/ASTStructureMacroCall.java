package nl.kiipdevelopment.sklectern.ast.macro;

import nl.kiipdevelopment.sklectern.ast.*;
import nl.kiipdevelopment.sklectern.ast.statement.ASTStatement;
import nl.kiipdevelopment.sklectern.ast.structure.ASTStructure;
import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import nl.kiipdevelopment.sklectern.parser.StructureMacro;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTStructureMacroCall(String name, ASTNodeList arguments) implements ASTStructure {
    @Override
    public @NotNull ASTNode shake() {
        return new ASTStructureMacroCall(name, new ASTNodeList(arguments.nodes()
                .stream()
                .map(ASTNode::shake)
                .filter(node -> !(node instanceof ASTEmpty))
                .toList()));
    }

    @Override
    public void check(@NotNull Context context) {
        for (ASTNode argument : arguments.nodes())
            argument.check(context);
        for (StructureMacro macro : context.structureMacros())
            if (macro.name().equals(name) && macro.parameters().size() != arguments.nodes().size())
                throw new ParseException("Macro '" + name + "' expected " + macro.parameters().size() + " arguments, " +
                        "but received " + arguments.nodes().size());
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        List<String> arguments = this.arguments.nodes().stream()
                .map(node -> node.visit(context).trim())
                .toList();

        for (StructureMacro macro : context.structureMacros())
            if (macro.name().equals(name))
                return macro.apply(arguments).visit(context);

        throw new ParseException("Reference to unknown macro '" + name + "'");
    }

    @Override
    public @NotNull List<ASTStatement> entries() {
        return List.of();
    }
}
