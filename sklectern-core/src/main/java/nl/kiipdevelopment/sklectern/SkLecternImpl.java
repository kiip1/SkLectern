package nl.kiipdevelopment.sklectern;

import nl.kiipdevelopment.sklectern.context.Config;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

final class SkLecternImpl implements SkLectern {
    static volatile SkLectern instance;

    static SkLectern instance() {
        SkLectern instance = SkLecternImpl.instance;
        if (instance == null)
            SkLecternImpl.instance = instance = new SkLecternImpl();
        return instance;
    }

    private final Config config = new Config(Path.of("scripts"), Path.of("dist"), false);
    private final ScriptManager scriptManager = new ScriptManager(config);

    @Override
    public @NotNull Config config() {
        return config;
    }

    @Override
    public @NotNull ScriptManager scriptManager() {
        return scriptManager;
    }
}
