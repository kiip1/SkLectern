package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.context.Config;
import nl.kiipdevelopment.sklectern.parser.ParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@ApiStatus.Experimental
public final class ScriptManager {
    private final Config config;

    ScriptManager(Config config) {
        this.config = config;
    }

    public void transformAll() {
        try (Stream<Path> stream = Files.walk(config.scriptFolder())) {
            stream.filter(path -> path.getFileName().toString().endsWith(".lsk"))
                    .parallel().forEach(this::transform);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }

    public void transform(String name) {
        transform(config.scriptFolder().resolve(name + ".lsk"));
    }

    public void transform(@NotNull Path path) {
        try {
            Script script = new Script(path);
            Files.writeString(config.distributionFolder()
                    .resolve(config.scriptFolder().relativize(path))
                    .resolveSibling(script.name() + ".l.sk"), script.transform());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
