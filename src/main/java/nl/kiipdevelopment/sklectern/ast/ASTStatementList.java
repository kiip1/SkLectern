package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public record ASTStatementList(List<ASTStatement> statements) implements ASTStatement {
    public ASTStatementList {
        statements = List.copyOf(statements);
    }

    @Override
    public void check(Context context) {
        for (ASTStatement statement : statements)
            statement.check(context);
    }

    @Override
    public List<String> get(Context context) {
        List<String> results = new ArrayList<>();
        for (ASTStatement statement : statements)
            results.addAll(statement.get(context));
        return results;
    }
}
