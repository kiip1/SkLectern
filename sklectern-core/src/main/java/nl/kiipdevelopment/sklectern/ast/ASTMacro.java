package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.Macro;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTMacro(String name, List<String> arguments, ASTStatementList statements) implements ASTStructure {
    public ASTMacro {
        arguments = List.copyOf(arguments);
    }

    @Override
    public void check(@NotNull Context context) {
        statements.check(context);
        context.macros(macros -> macros.add(new Macro(name, arguments, statements)));
    }

    @Override
    public @NotNull String visit(Context context) {
        return "";
    }

    @Override
    public List<ASTStatement> entries() {
        return List.of();
    }
}
