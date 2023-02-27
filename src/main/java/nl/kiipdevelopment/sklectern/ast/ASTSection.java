package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.stream.Stream;

@ApiStatus.Internal
public record ASTSection(ASTNode name, ASTStatementList statements) implements ASTStatement {
    @Override
    public void check(Context context) {
        statements.check(context);
    }

    @Override
    public List<String> get(Context context) {
        return Stream.concat(Stream.of(name.visit(context) + ":\n"), statements.get(context).stream()
                .map(line -> "\t" + line)).toList();
    }
}
