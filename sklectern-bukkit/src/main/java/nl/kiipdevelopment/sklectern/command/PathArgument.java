package nl.kiipdevelopment.sklectern.command;

import mx.kenzie.centurion.Argument;
import mx.kenzie.centurion.TypedArgument;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class PathArgument extends TypedArgument<Path> {
    private final Path folder;

    public static Argument<Path> path(@Nullable Path folder) {
        return new PathArgument(folder);
    }

    private PathArgument(@Nullable Path folder) {
        super(Path.class);
        this.folder = folder;
    }

    @Override
    public boolean matches(String string) {
        return string.length() > 0;
    }

    @Override
    public String[] possibilities() {
        if (folder == null) return super.possibilities();

        try (final Stream<Path> stream = Files.list(folder)) {
            return stream.map(path -> path.getFileName().toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path parse(String string) {
        return folder == null
                ? Path.of(string)
                : folder.resolve(string);
    }
}
