package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTMacroReference(String name, ASTNodeList arguments) implements ASTStatement {
    @Override
    public @NotNull ASTNode shake() {
        final ASTNode node = this.arguments.shake();
        if (node instanceof ASTEmpty) return new ASTEmpty();
        else if (!(node instanceof ASTNodeList args)) return new ASTMacroReference(name,
                new ASTNodeList(List.of(node)));
        else return new ASTMacroReference(name, args);
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
