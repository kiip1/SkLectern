package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.Macro;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTMacroReference(String name, List<ASTNode> arguments) implements ASTStatement {
    @Override
    public void check(@NotNull Context context) {
        for (ASTNode argument : arguments)
            argument.check(context);
        for (Macro macro : context.macros())
            if (macro.name().equals(name) && macro.parameters().size() != arguments.size())
                throw new ParseException("Macro " + name + " expected " + macro.parameters().size() + " arguments, " +
                        "but received " + arguments.size());
    }

    @Override
    public List<String> get(Context context) {
        List<String> arguments = this.arguments.stream()
                .map(node -> node.visit(context).trim())
                .toList();

        for (Macro macro : context.macros())
            if (macro.name().equals(name))
                return macro.apply(arguments).get(context);

        throw new ParseException("Reference to unknown macro " + name);
    }
}
