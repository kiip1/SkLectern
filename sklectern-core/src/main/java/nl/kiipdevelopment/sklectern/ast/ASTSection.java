package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

@ApiStatus.Internal
public record ASTSection(ASTNode name, ASTStatementList statements) implements ASTStatement {
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
