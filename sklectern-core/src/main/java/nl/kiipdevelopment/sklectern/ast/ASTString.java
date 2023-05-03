package nl.kiipdevelopment.sklectern.ast;

import nl.kiipdevelopment.sklectern.context.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@ApiStatus.Internal
public record ASTString(String value) implements ASTLiteral<String> {
    @Override
    public @NotNull String visit(@NotNull Context context) {
        final AtomicReference<String> reference = new AtomicReference<>(this.value);
        context.options().forEach((option, replacement) -> reference.getAndUpdate(value ->
                value.replaceAll(Pattern.quote("{@" + option + "}"), replacement.replaceAll("\"", "\"\""))));
        return reference.get();
    }
}
