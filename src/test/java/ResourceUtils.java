import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

final class ResourceUtils {
    private ResourceUtils() {}

    public static void extractResource(String source, @NotNull Path target) throws URISyntaxException, IOException {
        final URI uri = Objects.requireNonNull(TestPlugin.class.getResource("/" + source)).toURI();
        FileSystem fileSystem = null;

        // Only create a new filesystem if it's a jar file
        // (People can run this from their IDE too)
        if (uri.toString().startsWith("jar:"))
            fileSystem = FileSystems.newFileSystem(uri, Map.of("create", "true"));

        try {
            final Path jar = Paths.get(uri);
            if (Files.exists(target)) {
                try (Stream<Path> stream = Files.walk(target)) {
                    stream.sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                }
            }
            Files.walkFileTree(jar, new SimpleFileVisitor<>() {
                @Override
                public @NotNull FileVisitResult preVisitDirectory(@NotNull Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(target.resolve(jar.relativize(dir).toString()));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFile(@NotNull Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(jar.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } finally {
            if (fileSystem != null)
                fileSystem.close();
        }
    }
}
