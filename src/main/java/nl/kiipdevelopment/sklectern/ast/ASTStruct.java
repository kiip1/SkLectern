package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApiStatus.Internal
public record ASTStruct(String name, List<ASTStatement> entries) implements ASTStructure {
    public ASTStruct {
        entries = List.copyOf(entries);
    }

    @Override
    public void check(Context context) {
        for (ASTStatement entry : entries)
            entry.check(context);
    }

    @Override
    public @NotNull String visit(@NotNull Context context) {
        List<String> results = new ArrayList<>();
        for (ASTStatement entry : entries)
            results.addAll(entry.get(context));
        return name + ":\n" + results.stream().map(line -> "\t" + line).collect(Collectors.joining("\n"));
    }
}
