package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@ApiStatus.Internal
public record ASTFunctionReference(String name, ASTNodeList arguments) implements ASTNode {
    @Override
    public @NotNull ASTNode shake() {
        final ASTNode node = this.arguments.shake();
        if (node instanceof ASTEmpty) return new ASTEmpty();
        else if (!(node instanceof ASTNodeList args)) return new ASTFunctionReference(name,
                new ASTNodeList(List.of(node)));
        else return new ASTFunctionReference(name, args);
    }

    @Override
    public void check(@NotNull Context context) {
        for (ASTNode argument : arguments.nodes())
            argument.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        return name + "(" + this.arguments.nodes().stream()
                .map(node -> node.visit(context).trim())
                .collect(Collectors.joining(",")) + ")";
    }
}
