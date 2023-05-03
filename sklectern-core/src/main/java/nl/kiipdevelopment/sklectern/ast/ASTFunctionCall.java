package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

@ApiStatus.Internal
public record ASTFunctionCall(String name, ASTNodeList arguments) implements ASTNode {
    @Override
    public @NotNull ASTNode shake() {
        return new ASTFunctionCall(name, new ASTNodeList(arguments.nodes()
                .stream()
                .map(ASTNode::shake)
                .filter(node -> !(node instanceof ASTEmpty))
                .toList()));
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
