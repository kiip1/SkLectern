package nl.kiipdevelopment.sklectern.ast.statement;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

@ApiStatus.Internal
public record ASTSection(ASTNode name, ASTStatement statements) implements ASTStatement {
    @Override
    public @NotNull ASTNode shake() {
        final ASTNode name = this.name.shake();
        final ASTNode statements = this.statements.shake();

        if (statements instanceof ASTEmpty) return new ASTEmpty();
        else if (!(statements instanceof ASTStatement statement)) return new ASTEmpty();
        else if (name instanceof ASTEmpty) return statements;
        else return new ASTSection(name, statement);
    }

    @Override
    public void check(@NotNull Context context) {
        statements.check(context);
    }

    @Override
    public @NotNull List<String> get(@NotNull Context context) {
        return Stream.concat(Stream.of(name.visit(context) + ":\n"), statements.get(context).stream()
                .map(line -> "\t" + line)).toList();
    }
}
