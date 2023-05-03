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

import static com.google.common.io.MoreFiles.getNameWithoutExtension;

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
        transformAll(config.scriptFolder(), config.distributionFolder());
    }

    /**
     * @throws ParseException If parsing fails
     * @throws UncheckedIOException If there is an IO exception
     */
    public void transformAll(@NotNull Path source, @NotNull Path destination) {
        try (Stream<Path> stream = Files.walk(source)) {
            stream.filter(path -> path.getFileName().toString().endsWith(".lsk"))
                    .parallel().forEach(path -> this.transform(path,
                            destination.resolve(getNameWithoutExtension(path.getFileName()) + ".l.sk")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @throws ParseException If parsing fails
     * @throws UncheckedIOException If there is an IO exception
     */
    public void transform(String name) {
        transform(config.scriptFolder().resolve(name + ".lsk"),
                config.distributionFolder().resolve(name + ".l.sk"));
    }

    /**
     * @throws ParseException If parsing fails
     * @throws UncheckedIOException If there is an IO exception
     */
    public void transform(@NotNull Path source, @NotNull Path destination) {
        try {
            Script script = new Script(source);
            Files.writeString(destination, script.transform());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
