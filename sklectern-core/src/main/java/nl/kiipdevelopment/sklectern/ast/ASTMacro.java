package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import nl.kiipdevelopment.sklectern.parser.Macro;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record ASTMacro(String name, List<String> arguments, ASTStatement statements) implements ASTStructure {
    public ASTMacro {
        arguments = List.copyOf(arguments);
    }

    @Override
    public @NotNull ASTNode shake() {
        final ASTNode statements = this.statements.shake();

        if (statements instanceof ASTEmpty) return new ASTEmpty();
        else if (!(statements instanceof ASTStatement statement)) return new ASTEmpty();
        else return new ASTMacro(name, arguments, statement);
    }

    @Override
    public void check(@NotNull Context context) {
        statements.check(context);
        context.macros(macros -> macros.add(new Macro(name, arguments, statements)));
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
