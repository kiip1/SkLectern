package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.context.Config;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * The script manager allows for quick transformation of all scripts.
 */
@ApiStatus.Experimental
public final class ScriptManager {
    private final Config config;

    ScriptManager(Config config) {
        this.config = config;
    }

    /**
     * @throws ParseException If parsing fails
     * @throws UncheckedIOException If there is an IO exception
     */
    public void transformAll() {
        transformAll(config.scriptFolder());
    }

    /**
     * @throws ParseException If parsing fails
     * @throws UncheckedIOException If there is an IO exception
     */
    public void transformAll(@NotNull Path folder) {
        try (Stream<Path> stream = Files.walk(config.scriptFolder().relativize(folder))) {
            stream.filter(path -> path.getFileName().toString().endsWith(".lsk"))
                    .parallel().forEach(this::transform);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @throws ParseException If parsing fails
     * @throws UncheckedIOException If there is an IO exception
     */
    public void transform(String name) {
        transform(config.scriptFolder().resolve(name + ".lsk"));
    }

    /**
     * @throws ParseException If parsing fails
     * @throws UncheckedIOException If there is an IO exception
     */
    public void transform(@NotNull Path path) {
        try {
            Script script = new Script(path);
            Files.writeString(config.distributionFolder()
                    .resolve(config.scriptFolder().relativize(path))
                    .resolveSibling(script.name() + ".l.sk"), script.transform());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
