package nl.kiipdevelopment.sklectern.command;

import mx.kenzie.centurion.Argument;
import mx.kenzie.centurion.TypedArgument;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public final class PathArgument extends TypedArgument<Path> {
    public static final Argument<Path> PATH = new PathArgument();

    private PathArgument() {
        super(Path.class);
    }

    @Override
    public boolean matches(String string) {
        return string.length() > 0;
    }

    @Override
    public Path parse(String string) {
        return Path.of(string);
    }
}
