package nl.kiipdevelopment.sklectern.ast.statement;

import nl.kiipdevelopment.sklectern.ast.ASTEmpty;
import nl.kiipdevelopment.sklectern.ast.ASTNode;
import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public record ASTStatementList(List<ASTStatement> statements) implements ASTStatement {
    public ASTStatementList {
        statements = List.copyOf(statements);
    }

    @Override
    public @NotNull ASTNode shake() {
        final List<ASTStatement> statements = this.statements
                .stream()
                .map(ASTNode::shake)
                .filter(node -> !(node instanceof ASTEmpty))
                .filter(node -> node instanceof ASTStatement)
                .map(node -> (ASTStatement) node)
                .toList();

        if (statements.size() == 0) return new ASTEmpty();
        else if (statements.size() == 1) return statements.get(0);
        else return new ASTStatementList(statements);
    }

    @Override
    public void check(@NotNull Context context) {
        for (ASTStatement statement : statements)
            statement.check(context);
    }

    @Override
    public @NotNull List<String> get(@NotNull Context context) {
        List<String> results = new ArrayList<>();
        for (ASTStatement statement : statements)
            results.addAll(statement.get(context));
        return results;
    }
}
