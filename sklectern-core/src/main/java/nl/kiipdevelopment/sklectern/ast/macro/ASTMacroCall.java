package nl.kiipdevelopment.sklectern.ast.macro;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.ast.ASTNodeList;
import nl.kiipdevelopment.sklectern.ast.statement.ASTStatement;
import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTMacroCall(String name, ASTNodeList arguments) implements ASTStatement {
    @Override
    public @NotNull ASTNode shake() {
        return new ASTMacroCall(name, new ASTNodeList(arguments.nodes()
                .stream()
                .map(ASTNode::shake)
                .filter(node -> !(node instanceof ASTEmpty))
                .toList()));
    }

    @Override
    public void check(@NotNull Context context) {
        for (ASTNode argument : arguments.nodes())
            argument.check(context);
        for (Macro macro : context.macros())
            if (macro.name().equals(name) && macro.parameters().size() != arguments.nodes().size())
                throw new ParseException("Macro '" + name + "' expected " + macro.parameters().size() + " arguments, " +
                        "but received " + arguments.nodes().size());
    }

    @Override
    public @NotNull List<String> get(@NotNull Context context) {
        List<String> arguments = this.arguments.nodes().stream()
                .map(node -> node.visit(context).trim())
                .toList();

        for (Macro macro : context.macros())
            if (macro.name().equals(name))
                return macro.apply(arguments).get(context);

        throw new ParseException("Reference to unknown macro '" + name + "'");
    }
}
